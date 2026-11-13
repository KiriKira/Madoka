package com.callagent.host.calls

import org.junit.Assert.*
import org.junit.Test

class PlatformActivationTest {
    @Test fun delayedAnswerOrResumeCannotUndoNewerSystemHold() {
        val platform = PlatformActivation()
        val answer = platform.beginRequest()
        platform.observe(false)
        assertFalse(platform.grant(answer))
        assertFalse(platform.active)
        val resume = platform.beginRequest()
        assertTrue(platform.grant(resume))
        assertTrue(platform.active)
    }
    @Test fun olderOperationCannotOverrideNewerPlatformSequence() {
        val platform = PlatformActivation()
        val request = platform.beginRequest()
        platform.observe(true)
        platform.observe(false)
        assertFalse(platform.grant(request))
        assertFalse(platform.active)
    }
}
