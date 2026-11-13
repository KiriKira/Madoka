package com.callagent.host.calls

import android.content.Context
import android.telecom.DisconnectCause
import com.callagent.host.BuildConfig

/** One backend per build. Rollback builds select legacy before any call is registered. */
internal object CallTelecomController {
    @androidx.annotation.VisibleForTesting
    internal var coreForTests: Boolean? = null
    private val core get() = coreForTests ?: BuildConfig.CORE_TELECOM
    fun ensurePhoneAccount(context: Context) {
        if (core) CoreTelecomAdapter.initialize(context) else LegacyCallTelecomController.ensurePhoneAccount(context)
    }
    fun addIncoming(context: Context, details: TelecomCallDetails) {
        if (core) CoreTelecomAdapter.add(context, details) else LegacyCallTelecomController.addIncoming(context, details)
    }
    fun placeOutgoing(context: Context, details: TelecomCallDetails) {
        if (core) CoreTelecomAdapter.add(context, details) else LegacyCallTelecomController.placeOutgoing(context, details)
    }
    fun isRegistered(callId: String): Boolean = !core || CoreTelecomAdapter.isRegistered(callId)
    fun setActive(callId: String) {
        if (CallRuntime.snapshot(callId)?.held == true) return
        if (core) CoreTelecomAdapter.activate(callId) else LegacyCallTelecomController.setActive(callId)
    }
    fun resume(callId: String) {
        if (core) CoreTelecomAdapter.activate(callId) else LegacyCallTelecomController.setActive(callId)
    }
    fun answer(callId: String, accepted: () -> Unit) {
        if (core) CoreTelecomAdapter.answer(callId, accepted) else accepted()
    }
    fun setDialing(callId: String) { if (!core) LegacyCallTelecomController.setDialing(callId) }
    fun disconnect(callId: String, cause: Int = DisconnectCause.REMOTE, complete: () -> Unit = {}) {
        if (core) CoreTelecomAdapter.disconnect(callId, cause, complete)
        else { LegacyCallTelecomController.disconnect(callId, cause); complete() }
    }
    fun audioEndpoints(callId: String) = if (core) CoreTelecomAdapter.endpoints(callId).map { it.type }.distinct()
        else LegacyCallTelecomController.audioEndpoints(callId)
    fun devices(callId: String): List<AudioDevice> = if (core) CoreTelecomAdapter.endpoints(callId)
        else audioEndpoints(callId).map { AudioDevice(it.name, it.displayName(), it) }
    fun currentDevice(callId: String): AudioDevice? = if (core) CoreTelecomAdapter.current(callId)
        else currentAudioEndpoint(callId)?.let { AudioDevice(it.name, it.displayName(), it) }
    fun currentAudioEndpoint(callId: String) = if (core) CoreTelecomAdapter.current(callId)?.type
        else LegacyCallTelecomController.currentAudioEndpoint(callId)
    fun selectAudioEndpoint(callId: String, endpoint: CallAudioEndpoint): Boolean {
        if (!core) return LegacyCallTelecomController.selectAudioEndpoint(callId, endpoint)
        val device = devices(callId).firstOrNull { it.type == endpoint } ?: return false
        return selectDevice(callId, device.id)
    }
    fun selectDevice(callId: String, id: String): Boolean = if (core) CoreTelecomAdapter.select(callId, id)
        else devices(callId).firstOrNull { it.id == id }?.let { LegacyCallTelecomController.selectAudioEndpoint(callId, it.type) } ?: false
}

internal data class AudioDevice(val id: String, val name: String, val type: CallAudioEndpoint)
