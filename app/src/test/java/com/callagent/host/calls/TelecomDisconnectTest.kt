package com.callagent.host.calls

import android.os.ParcelUuid
import android.telecom.DisconnectCause
import androidx.core.telecom.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.coroutines.EmptyCoroutineContext

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class TelecomDisconnectTest {
    private class Control : CallControlScope {
        override val coroutineContext = EmptyCoroutineContext
        val invoked = CompletableDeferred<Unit>()
        val result = CompletableDeferred<Unit>()
        val causes = mutableListOf<Int>()
        override fun getCallId() = ParcelUuid(java.util.UUID.randomUUID())
        override suspend fun setActive() = CallControlResult.Success()
        override suspend fun setInactive() = CallControlResult.Success()
        override suspend fun answer(callType: Int) = CallControlResult.Success()
        override suspend fun disconnect(disconnectCause: DisconnectCause): CallControlResult {
            causes.add(disconnectCause.code)
            invoked.complete(Unit)
            result.await()
            return CallControlResult.Success()
        }
        override suspend fun requestEndpointChange(endpoint: CallEndpointCompat) = CallControlResult.Success()
        override val currentCallEndpoint = emptyFlow<CallEndpointCompat>()
        override val availableEndpoints = emptyFlow<List<CallEndpointCompat>>()
        override val isMuted = emptyFlow<Boolean>()
    }
    @Test fun earlyHangupWaitsForRegistrationAndDisconnectResultBeforeTeardown() = runBlocking {
        val ready = CompletableDeferred<CallControlScope>()
        val control = Control()
        var stopped = false
        val close = launch(start = CoroutineStart.UNDISPATCHED) {
            disconnectTelecom(ready, DisconnectCause.ERROR) { false }
            stopped = true
        }
        assertFalse(stopped)
        assertFalse(ready.isCancelled)
        ready.complete(control)
        control.invoked.await()
        assertFalse("service must stay until platform result", stopped)
        assertEquals(listOf(DisconnectCause.LOCAL), control.causes)
        control.result.complete(Unit)
        close.join()
        assertTrue(stopped)
        assertFalse(ready.isCancelled)
    }
    @Test fun failedRegistrationCompletesTeardownWithoutCancellingItsOwner() = runBlocking {
        val ready = CompletableDeferred<CallControlScope>()
        ready.completeExceptionally(IllegalStateException("registration denied"))
        val result = runCatching { disconnectTelecom(ready, DisconnectCause.LOCAL) { false } }
        assertTrue(result.isFailure)
        assertTrue(currentCoroutineContext().isActive)
    }
}
