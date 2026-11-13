package com.callagent.host.calls

import org.junit.Assert.*
import org.junit.Test

class CallAudioPermitTest {
    @Test fun networkRecoveryCannotUndoTelecomHold() {
        val audio = CallAudioPermit()
        audio.authorize(true); audio.activate(true)
        assertTrue(audio.allowed)
        audio.activate(false) // SIM call accepted
        audio.authorize(false) // network lost
        audio.authorize(true) // authorized network recovery / FGS ready
        assertFalse(audio.allowed)
        audio.activate(true)
        assertTrue(audio.allowed)
    }
    @Test fun resumeCannotUndoNetworkOrForegroundSuspension() {
        val audio = CallAudioPermit()
        audio.authorize(true); audio.activate(true)
        audio.authorize(false); audio.activate(false)
        audio.activate(true)
        assertFalse(audio.allowed)
        audio.authorize(true)
        assertTrue(audio.allowed)
    }
    @Test fun terminalCallCannotBeRevivedByLateCallbacks() {
        val audio = CallAudioPermit()
        audio.authorize(true); audio.activate(true); audio.end()
        audio.activate(false); audio.authorize(false)
        audio.activate(true); audio.authorize(true)
        assertFalse(audio.allowed)
    }
    @Test fun foregroundAuthorizationAloneNeverOpensMicrophone() {
        val audio = CallAudioPermit()
        audio.authorize(true)
        assertFalse(audio.allowed)
    }
}
