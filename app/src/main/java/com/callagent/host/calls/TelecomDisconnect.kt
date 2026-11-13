package com.callagent.host.calls

import android.telecom.DisconnectCause
import androidx.core.telecom.CallControlResult
import androidx.core.telecom.CallControlScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withTimeout

/** Closing before registration waits for the actual control; it never cancels addCall. */
internal suspend fun disconnectTelecom(
    ready: Deferred<CallControlScope>,
    cause: Int,
    alreadyClosed: () -> Boolean
) {
    val control = withTimeout(6000) { ready.await() }
    var result = withTimeout(4500) { control.disconnect(DisconnectCause(CoreTelecomAdapter.disconnectCause(cause))) }
    if (result is CallControlResult.Error && !alreadyClosed()) {
        result = withTimeout(4500) { control.disconnect(DisconnectCause(DisconnectCause.LOCAL)) }
    }
    check(result is CallControlResult.Success || alreadyClosed()) { "Telecom disconnect rejected" }
}
