package com.callagent.host.data

import com.callagent.host.RobolectricAndroidKeyStoreProvider
import com.callagent.host.TestAndroidKeyStoreBacking
import com.callagent.rtcmedia.*
import org.json.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.security.*
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[34])
class RelayRepositoryTest {
 private val app=RuntimeEnvironment.getApplication()
 private var previous:Provider?=null
 @Before fun before(){previous=Security.getProvider("AndroidKeyStore");Security.removeProvider("AndroidKeyStore");TestAndroidKeyStoreBacking.keys.clear();Security.insertProviderAt(RobolectricAndroidKeyStoreProvider(),1);RelayRepository.reset(app);RelayRepository.retainNodes(emptySet())}
 @After fun after(){RelayRepository.reset(app);RelayRepository.retainNodes(emptySet());Security.removeProvider("AndroidKeyStore");previous?.let{Security.insertProviderAt(it,1)}}
 @Test fun sourceTokensAreEncryptedAndCorruptStorageRequiresReset(){
  val secret="test-only-source-token"
  RelayRepository.save(app,JSONArray().put(JSONObject().put("id","external").put("type","external").put("url","https://relay.example").put("token",secret)))
  assertFalse(app.getSharedPreferences("rtc-relay-sources",0).all.values.joinToString().contains(secret))
  assertEquals(secret,RelayRepository.load(app).getJSONObject(0).getString("token"))
  app.getSharedPreferences("rtc-relay-sources",0).edit().putString("config","AA==").commit()
  try{RelayRepository.load(app);fail()}catch(e:IllegalStateException){assertEquals("relay_store_unavailable",e.message)}
  assertTrue(RelayRepository.resolve(app,"owner").isEmpty())
  RelayRepository.reset(app);assertEquals(0,RelayRepository.load(app).length())
 }
 @Test fun accountChangeAndAuthorizationRejectionInvalidateCredentialCache(){
  val calls=AtomicInteger();var reject=false
  RelayRepository.registerNode("node-test","test"){
   calls.incrementAndGet();if(reject)throw MediaApiException(401,"SESSION_REVOKED")
   JSONObject().put("ice_servers",JSONArray().put(JSONObject().put("urls",JSONArray().put("turn:127.0.0.1:9?transport=udp")).put("username","user"+calls.get()).put("credential","test-only"))).put("expires_at",Instant.now().plusSeconds(600).toString())
  }
  RelayRepository.save(app,JSONArray().put(JSONObject().put("id","node-test").put("type","node").put("enabled",true)))
  assertEquals("user1",RelayRepository.resolve(app,"first-owner").single().servers.single().username)
  assertEquals("user2",RelayRepository.resolve(app,"second-owner").single().servers.single().username)
  reject=true;assertTrue(RelayRepository.resolve(app,"second-owner").isEmpty())
  assertEquals(3,calls.get())
 }
 @Test fun allSourcesShareOnePreparationDeadline(){
  val config=JSONArray()
  repeat(7){i->val id="node-$i";RelayRepository.registerNode(id,id){Thread.sleep(10000);null};config.put(JSONObject().put("id",id).put("type","node").put("enabled",true))}
  RelayRepository.save(app,config)
  val started=System.nanoTime();assertTrue(RelayRepository.resolve(app,"bounded-owner").isEmpty())
  assertTrue("deadline grew with source count",(System.nanoTime()-started)/1_000_000L<2500)
 }
}
