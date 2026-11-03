package com.callagent.host.calls

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class NotConnectedNoticeTest {
    @Test fun anAnswerTheGatewayEndedBeforeConnectingIsExplained() {
        assertNotNull(HostCallService.notConnectedNotice(CallDirection.INCOMING, CallPhase.ANSWERING))
        assertNotNull(HostCallService.notConnectedNotice(CallDirection.OUTGOING, CallPhase.DIALING))
        assertNotNull(HostCallService.notConnectedNotice(CallDirection.OUTGOING, CallPhase.OUTBOUND_RINGING))
    }

    @Test fun aConnectedOrUnansweredCallKeepsThePlainEnd() {
        assertNull(HostCallService.notConnectedNotice(CallDirection.INCOMING, CallPhase.ACTIVE))
        assertNull(HostCallService.notConnectedNotice(CallDirection.OUTGOING, CallPhase.ACTIVE))
        assertNull(HostCallService.notConnectedNotice(CallDirection.INCOMING, CallPhase.INCOMING_RINGING))
    }
}
