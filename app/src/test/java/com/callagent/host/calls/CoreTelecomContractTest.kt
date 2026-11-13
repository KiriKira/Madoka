package com.callagent.host.calls

import android.telecom.DisconnectCause
import androidx.core.telecom.CallAttributesCompat
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class CoreTelecomContractTest {
    @Test fun destinationNeverBecomesAPhoneUriPassedToLocalTelecom() {
        for (number in listOf("112", "911", "*123#", "+85212345678")) {
            val attributes = CoreTelecomAdapter.attributes(TelecomCallDetails("opaque-call-id", CallDirection.OUTGOING, number, "远程 SIM"))
            assertEquals("gsm2sip", attributes.address.scheme)
            assertEquals("opaque-call-id", attributes.address.schemeSpecificPart)
            assertFalse(attributes.address.toString().contains(number))
            assertEquals(CallAttributesCompat.DIRECTION_OUTGOING, attributes.direction)
        }
    }
    @Test fun unsupportedHoldNeverLeaksThroughLibraryDefaultCapabilities() {
        val details = TelecomCallDetails("id", CallDirection.INCOMING, null, "SIM")
        assertEquals(0, CoreTelecomAdapter.attributes(details, false).callCapabilities)
        assertEquals(CallAttributesCompat.SUPPORTS_SET_INACTIVE, CoreTelecomAdapter.attributes(details, true).callCapabilities)
    }
    @Test fun allLegacyDisconnectCausesMapToLibrarySupportedValues() {
        val allowed = setOf(DisconnectCause.LOCAL, DisconnectCause.REMOTE, DisconnectCause.REJECTED, DisconnectCause.MISSED)
        for (cause in 0..15) assertTrue(CoreTelecomAdapter.disconnectCause(cause) in allowed)
        assertEquals(DisconnectCause.LOCAL, CoreTelecomAdapter.disconnectCause(DisconnectCause.ERROR))
        for (cause in allowed) assertEquals(cause, CoreTelecomAdapter.disconnectCause(cause))
    }
}
