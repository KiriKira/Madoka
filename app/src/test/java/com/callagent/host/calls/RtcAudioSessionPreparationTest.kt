package com.callagent.host.calls

import com.callagent.rtcmedia.MediaApi
import com.callagent.rtcmedia.RtcAudioSession
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * A preparation made while a call rings shares its leg with the session that
 * answers. It must never signal on that leg: not when it is discarded, and
 * not when it fails (here the native library is absent, so it always fails).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class RtcAudioSessionPreparationTest {
    private val requests = Collections.synchronizedList(mutableListOf<String>())
    private val api = MediaApi { method, path, body ->
        requests += "$method $path ${body?.optString("kind").orEmpty()}"
        JSONObject()
    }

    @Test fun aFailedPreparationSignalsNothingOnTheLeg() {
        val failed = CountDownLatch(1)
        val session = session { failed.countDown() }
        session.prepare()
        assertTrue("preparation without the native library must fail", failed.await(5, TimeUnit.SECONDS))
        assertTrue(waitClosed(session))
        assertEquals(emptyList<String>(), requests.toList())
    }

    @Test fun aDiscardedPreparationSignalsNothingOnTheLeg() {
        val session = session {}
        session.prepare()
        session.discard()
        assertTrue(waitClosed(session))
        Thread.sleep(300)
        assertEquals(emptyList<String>(), requests.toList())
    }

    // Regression cases authored for CI; not compiled or run in the repair workspace.
    @Test fun closingAnUnusedSessionDoesNotSignalOnTheSharedLeg() {
        val session = session {}
        session.close()
        assertTrue(session.awaitShutdown(5000))
        assertTrue(session.isClosed)
        assertEquals(emptyList<String>(), requests.toList())
    }

    @Test fun preparationFailureCallbackMayCloseWithoutSignalingOnTheSharedLeg() {
        val failed = CountDownLatch(1)
        lateinit var session: RtcAudioSession
        session = session {
            session.close()
            failed.countDown()
        }
        session.prepare()
        assertTrue("preparation without the native library must fail", failed.await(5, TimeUnit.SECONDS))
        assertTrue(session.awaitShutdown(5000))
        assertTrue(session.isClosed)
        assertEquals(emptyList<String>(), requests.toList())
    }

    private fun waitClosed(session: RtcAudioSession): Boolean {
        val until = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        while (System.nanoTime() < until) { if (session.isClosed) return true; Thread.sleep(20) }
        return session.isClosed
    }

    private fun session(onFailure: (String) -> Unit) = RtcAudioSession(
        RuntimeEnvironment.getApplication(),
        JSONObject().put("call_control", "rtc").put("ice_servers", JSONArray())
            .put("binding", JSONObject()
                .put("call_id", "b86c8f0d-084d-4b89-9c0d-504577c00313")
                .put("leg_id", "0b3a0c55-6f0e-4d32-9a51-6d9f6b1f0a01")
                .put("attempt_id", "6a1c2f1e-61f4-4b7b-8c55-0a3d3d1b2e02")
                .put("gateway_id", "1f2e3d4c-5b6a-4978-8a9b-0c1d2e3f4a03")
                .put("client_id", "2a3b4c5d-6e7f-4081-9a2b-3c4d5e6f7a04")),
        api, false, onFailure = onFailure
    )
}
