package com.callagent.host.background

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.SystemClock
import com.callagent.host.RobolectricAndroidKeyStoreProvider
import com.callagent.host.TestAndroidKeyStoreBacking
import com.callagent.host.data.HostSession
import com.callagent.host.data.SessionStore
import com.callagent.host.data.databaseName
import okhttp3.*
import okhttp3.mockwebserver.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkInfo
import java.security.Provider
import java.security.Security
import java.util.UUID
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[34])
class HostWakeRecoveryTest {
    private val app=RuntimeEnvironment.getApplication()
    private var previous:Provider?=null
    @Before fun setup(){
        previous=Security.getProvider("AndroidKeyStore");Security.removeProvider("AndroidKeyStore")
        TestAndroidKeyStoreBacking.keys.clear();Security.insertProviderAt(RobolectricAndroidKeyStoreProvider(),1)
        val network=shadowOf(app.getSystemService(ConnectivityManager::class.java))
        network.setActiveNetworkInfo(ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED,ConnectivityManager.TYPE_WIFI,0,true,true))
        network.setDefaultNetworkActive(true)
    }
    @After fun cleanup(){RuntimeState.preferences(app).edit().clear().commit();SessionStore(app).clear();Security.removeProvider("AndroidKeyStore");previous?.let{Security.insertProviderAt(it,1)}}
    private fun field(name:String)=HostBackgroundService::class.java.getDeclaredField(name).apply{isAccessible=true}
    private fun method(name:String,vararg types:Class<*>)=HostBackgroundService::class.java.getDeclaredMethod(name,*types).apply{isAccessible=true}
    private fun bind(base:String):HostSession {
        val session=HostSession(UUID.randomUUID().toString(),base,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"client","old-token","2099-01-01T00:00:00Z","refresh-token","2099-02-01T00:00:00Z")
        SessionStore(app).replace(session)
        RuntimeState.preferences(app).edit().putBoolean(RuntimeState.KEY_ENABLED,true).putString(RuntimeState.KEY_SESSION_STAMP,sessionStamp(session)).putString(RuntimeState.KEY_SESSION_DB_NAME,session.databaseName()).commit()
        return session
    }
    private fun http()=MockResponse().setHeader("Content-Type","application/json").setBody("""{"items":[],"next_cursor":null,"resync_required":false,"extensions":[],"rtc_call_control":false,"push":false}""")

    @Test fun networkAvailableExpeditesMinutesOfBackoffAndActuallyConnects() {
        MockWebServer().use{server->
            val opened=CountDownLatch(1)
            server.dispatcher=object:okhttp3.mockwebserver.Dispatcher(){override fun dispatch(r:RecordedRequest)=if(r.path=="/v1/ws")MockResponse().withWebSocketUpgrade(object:WebSocketListener(){override fun onOpen(ws:WebSocket,response:Response){opened.countDown()}})else http()}
            bind(server.url("/v1").toString())
            val controller=Robolectric.buildService(HostBackgroundService::class.java).create();val service=controller.get()
            try{
                (field("reconnectAttempt").get(service) as AtomicInteger).set(8)
                method("scheduleReconnectWithBackoff").invoke(service)
                assertTrue(field("reconnectNotBefore").getLong(service)-SystemClock.elapsedRealtime()>120_000)
                val pendingDeadline=field("reconnectNotBefore").getLong(service)
                method("scheduleWebSocketReconnect",Long::class.javaPrimitiveType!!,Boolean::class.javaPrimitiveType!!).invoke(service,500L,false)
                assertEquals("ordinary scheduling must retain the pending backoff",pendingDeadline,field("reconnectNotBefore").getLong(service))
                val started=System.nanoTime()
                (field("networkCallback").get(service) as ConnectivityManager.NetworkCallback).onAvailable(org.robolectric.shadows.ShadowNetwork.newInstance(901))
                assertTrue("network recovery must perform a new wake handshake after its 2 s settle",opened.await(4,TimeUnit.SECONDS))
                assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-started)<4000)
                assertTrue("opening alone must not clear unstable-connection backoff",(field("reconnectAttempt").get(service) as AtomicInteger).get()>=8)
            }finally{controller.destroy()}
        }
    }

    @Test fun authorizationFailureWaitsForRefreshThenReconnectsWithOnlyTheNewToken() {
        MockWebServer().use{server->
            val opened=CountDownLatch(1);val wakeTokens=java.util.Collections.synchronizedList(mutableListOf<String>())
            server.dispatcher=object:okhttp3.mockwebserver.Dispatcher(){override fun dispatch(r:RecordedRequest):MockResponse {
                if(r.path=="/v1/ws"){
                    wakeTokens.add(r.getHeader("Authorization").orEmpty())
                    if(r.getHeader("Authorization")=="Bearer old-token")return MockResponse().setResponseCode(401)
                    return MockResponse().withWebSocketUpgrade(object:WebSocketListener(){override fun onOpen(ws:WebSocket,response:Response){opened.countDown()}})
                }
                if(r.path=="/v1/auth/refresh")return MockResponse().setHeader("Content-Type","application/json").setBody("""{"access_token":"new-token","refresh_token":"new-refresh","access_expires_at":"2099-01-01T00:00:00Z","refresh_expires_at":"2099-02-01T00:00:00Z"}""")
                if(r.getHeader("Authorization")=="Bearer old-token")return MockResponse().setResponseCode(401).setBody("""{"error":{"code":"INVALID_CREDENTIALS"}}""")
                return http()
            }}
            bind(server.url("/v1").toString())
            val controller=Robolectric.buildService(HostBackgroundService::class.java).create();val service=controller.get()
            try{
                (field("reconnectAttempt").get(service) as AtomicInteger).set(8)
                method("ensureWebSocket").invoke(service)
                assertTrue("successful refresh must not wait the existing minutes-long backoff",opened.await(5,TimeUnit.SECONDS))
                assertEquals(listOf("Bearer old-token","Bearer new-token"),wakeTokens.toList())
                assertFalse((field("authReconnectPending").get(service) as AtomicBoolean).get())
            }finally{controller.destroy()}
        }
    }

    @Test fun networkRecoveryCannotBypassServerRetryAfter() {
        MockWebServer().use{server->
            val handshakes=AtomicInteger()
            server.dispatcher=object:okhttp3.mockwebserver.Dispatcher(){override fun dispatch(r:RecordedRequest)=if(r.path=="/v1/ws"){
                handshakes.incrementAndGet();MockResponse().setResponseCode(429).setHeader("Retry-After","60").setBody("""{"error":{"code":"RATE_LIMITED"}}""")
            }else http()}
            bind(server.url("/v1").toString())
            val controller=Robolectric.buildService(HostBackgroundService::class.java).create();val service=controller.get()
            try{
                method("ensureWebSocket").invoke(service)
                val deadline=System.nanoTime()+TimeUnit.SECONDS.toNanos(3)
                while(field("retryAfterNotBefore").getLong(service)==0L&&System.nanoTime()<deadline)Thread.sleep(10)
                assertTrue(field("retryAfterNotBefore").getLong(service)-SystemClock.elapsedRealtime()>50_000)
                (field("networkCallback").get(service) as ConnectivityManager.NetworkCallback).onAvailable(org.robolectric.shadows.ShadowNetwork.newInstance(902))
                Thread.sleep(2300)
                assertEquals(1,handshakes.get())
            }finally{controller.destroy()}
        }
    }
}
