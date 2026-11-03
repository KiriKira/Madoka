package com.callagent.host.data

import org.junit.Assert.assertEquals
import org.junit.Test

class CallHistoryForkTest {
    private fun call(
        id: String, gateway: String, at: String, from: String = "+8613800000000",
        answeredAt: String? = null, reason: String? = null, direction: String = "incoming"
    ) = RemoteCall(
        callId = id, gatewayId = gateway, clientId = null, simId = "sim", mappingRevision = 1,
        direction = direction, state = "ended", stateRevision = 3, from = from, to = null,
        createdAt = at, expiresAt = null, answeredAt = answeredAt, endedAt = null, reason = reason
    )

    @Test fun theNodeThatLostDoesNotAddAnAnsweredElsewhereRow() {
        val answeredHere = call("a", "node-a", "2026-11-03T10:51:45Z", answeredAt = "2026-11-03T10:51:53Z")
        val otherNode = call("b", "node-b", "2026-11-03T10:51:46Z", reason = "answered_elsewhere")
        assertEquals(listOf("a"), CallHistoryCache.collapseForks(listOf(otherNode, answeredHere)).map { it.callId })
    }

    @Test fun aCallAnsweredOnAnotherPhoneStillShowsOnce() {
        val calls = listOf(
            call("a", "node-a", "2026-11-03T10:51:45Z", reason = "answered_elsewhere"),
            call("b", "node-b", "2026-11-03T10:51:47Z", reason = "answered_elsewhere")
        )
        assertEquals(1, CallHistoryCache.collapseForks(calls).size)
    }

    @Test fun separateCallsAreNeverMerged() {
        val calls = listOf(
            // Same node, same caller, seconds apart: two real calls.
            call("a", "node-a", "2026-11-03T10:51:45Z"),
            call("b", "node-a", "2026-11-03T10:51:55Z"),
            // Other node but a minute later: another call.
            call("c", "node-b", "2026-11-03T10:52:50Z"),
            // Other node, same time, different caller.
            call("d", "node-b", "2026-11-03T10:51:46Z", from = "+8613900000000"),
            // Outgoing calls are not forked.
            call("e", "node-b", "2026-11-03T10:51:45Z", direction = "outgoing"),
            call("f", "node-c", "2026-11-03T10:51:45Z", direction = "outgoing")
        )
        assertEquals(6, CallHistoryCache.collapseForks(calls).size)
    }
}

class HostRunLogMaskTest {
    @Test fun dialedNumbersAreMaskedInStatusLines() {
        assertEquals("正在呼叫 [号码]…", HostRunLog.maskNumbers("正在呼叫 +86 138-0000-0000…"))
        assertEquals("正在呼叫 [号码]…", HostRunLog.maskNumbers("正在呼叫 10086…"))
        assertEquals("通话中。", HostRunLog.maskNumbers("通话中。"))
    }
}
