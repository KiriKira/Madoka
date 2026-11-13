package com.callagent.host.calls

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.core.telecom.CallAttributesCompat
import androidx.core.telecom.CallControlResult
import androidx.core.telecom.CallControlScope
import androidx.core.telecom.CallEndpointCompat
import androidx.core.telecom.CallsManager
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/** Process lifetime owner: service/Activity teardown must not cancel an undisconnected call. */
internal object CoreTelecomAdapter {
    private val owner = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var manager: CallsManager? = null
    private val sessions = ConcurrentHashMap<String, Session>()
    private class Session(val context: Context, val details: TelecomCallDetails) {
        val ready = CompletableDeferred<CallControlScope>()
        val closed = CompletableDeferred<Unit>()
        val operations = Mutex()
        val activation = PlatformActivation()
        val observers = java.util.concurrent.CopyOnWriteArrayList<Job>()
        @Volatile var closing = false
        @Volatile var devices: List<CallEndpointCompat> = emptyList()
        @Volatile var current: CallEndpointCompat? = null
    }

    @Synchronized fun initialize(context: Context) {
        if (manager != null) return
        val app = context.applicationContext
        check(sessions.isEmpty())
        val telecom = app.getSystemService(TelecomManager::class.java)
        // Only the account owned by our old component; never SIM or other app accounts.
        val old = PhoneAccountHandle(ComponentName(app, HostCallConnectionService::class.java), "gsm2sip-remote-voice")
        if (telecom.getPhoneAccount(old) != null) {
            check(LegacyCallTelecomController.allConnections().isEmpty()) { "Old account still has a call" }
            telecom.unregisterPhoneAccount(old)
        }
        val next = CallsManager(app)
        next.registerAppWithTelecom(CallsManager.CAPABILITY_BASELINE)
        manager = next
    }

    internal fun attributes(details: TelecomCallDetails, holdEnabled: Boolean = true) = CallAttributesCompat(
        displayName = details.simLabel.ifBlank { "Madoka" },
        address = Uri.fromParts("gsm2sip", details.callId, null),
        direction = if (details.direction == CallDirection.INCOMING) CallAttributesCompat.DIRECTION_INCOMING else CallAttributesCompat.DIRECTION_OUTGOING,
        callType = CallAttributesCompat.CALL_TYPE_AUDIO_CALL,
        callCapabilities = if (holdEnabled) CallAttributesCompat.SUPPORTS_SET_INACTIVE else 0
    )

    fun isRegistered(id: String): Boolean = sessions[id]?.let { it.ready.isCompleted && !it.ready.isCancelled && !it.closing } == true

    fun add(context: Context, details: TelecomCallDetails) {
        initialize(context)
        val session = Session(context.applicationContext, details)
        check(sessions.putIfAbsent(details.callId, session) == null) { "Call already registered" }
        owner.launch {
            try {
                manager!!.addCall(attributes(details),
                    onAnswer = answerCallback@ {
                        checkCurrent(session)
                        if (CallRuntime.snapshot(details.callId)?.phase in setOf(CallPhase.ANSWERING, CallPhase.ACTIVE)) return@answerCallback
                        check(CallRuntime.snapshot(details.callId)?.phase == CallPhase.INCOMING_RINGING)
                        session.activation.observe(true)
                        CallRuntime.onTelecomActive(details.callId)
                        CallRuntime.onTelecomAnswer(details.callId)
                        check(CallRuntime.snapshot(details.callId)?.phase == CallPhase.ANSWERING)
                        // Network acceptance is asynchronous; business ACTIVE still comes from server.
                    },
                    onDisconnect = { cause ->
                        if (!session.closing) {
                            CallRuntime.snapshot(details.callId)?.let {
                                if (it.phase == CallPhase.INCOMING_RINGING) {
                                    if (cause.code == DisconnectCause.REJECTED) CallRuntime.onTelecomReject(details.callId)
                                    else CallRuntime.rejectFromService(details.callId, "系统已结束本机来电。")
                                } else CallRuntime.onTelecomDisconnect(details.callId)
                            }
                        }
                    },
                    onSetActive = { checkCurrent(session); session.activation.observe(true); CallRuntime.onTelecomActive(details.callId) },
                    onSetInactive = {
                        checkCurrent(session)
                        session.activation.observe(false)
                        // Keep revision and media suspension in the same callback turn.
                        CallRuntime.onTelecomHold(details.callId, true)
                    }
                ) {
                    session.ready.complete(this)
                    if (!session.closing) {
                        CallRuntime.onTelecomConnectionCreated(details.callId)
                        // No onShowIncomingCallUi in Core-Telecom; notification is explicit.
                        val call = CallRuntime.snapshot(details.callId)
                        if (call != null) {
                            if (call.phase == CallPhase.INCOMING_RINGING) {
                                CallNotificationManager.ensureIncoming(session.context, call)
                                if (CallRuntime.isAppVisible) session.context.startCallUi(call.callId)
                            }
                        }
                    }
                    session.observers += watch(session) { currentCallEndpoint.collect { endpoint ->
                        session.current = endpoint
                        if (!session.closing) CallRuntime.onTelecomAudioRoute(details.callId, endpoint.device().type)
                    } }
                    session.observers += watch(session) { availableEndpoints.collect { session.devices = it; CallRuntime.notifyChanged(details.callId) } }
                    session.observers += watch(session) { isMuted.collect { if (!session.closing) CallRuntime.onTelecomMute(details.callId, it) } }
                }
            } catch (failure: Exception) {
                Log.w("CoreTelecom", "add_call failed: ${failure.javaClass.simpleName}")
                session.ready.completeExceptionally(failure)
                if (session.ready.isCompleted && !session.ready.isCancelled) {
                    withContext(NonCancellable) {
                        runCatching {
                            withTimeout(4500) { session.ready.await().disconnect(DisconnectCause(DisconnectCause.LOCAL)) }
                        }
                    }
                }
                if (!session.closing) CallRuntime.onTelecomIncomingFailed(details.callId)
            } finally {
                session.observers.forEach { it.cancel() }
                session.closed.complete(Unit)
                sessions.remove(details.callId, session)
            }
        }
    }

    private fun watch(session: Session, action: suspend () -> Unit): Job = owner.launch {
        try { action() }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (failure: Exception) {
            Log.w("CoreTelecom", "observer failed: ${failure.javaClass.simpleName}")
            if (!session.closing) CallRuntime.onTelecomIncomingFailed(session.details.callId)
        }
    }

    private fun checkCurrent(session: Session) {
        check(!session.closing && CallRuntime.snapshot(session.details.callId)?.phase !in
            setOf(null, CallPhase.ENDED, CallPhase.FAILED, CallPhase.DISCONNECTING)) { "Call already ended" }
    }
    private fun operate(id: String, action: suspend (Session, CallControlScope) -> Unit) {
        val session = sessions[id] ?: return
        owner.launch {
            session.operations.withLock {
                if (session.closing) return@withLock
                try {
                    val control = withTimeout(5000) { session.ready.await() }
                    checkCurrent(session)
                    action(session, control)
                } catch (failure: Exception) {
                    Log.w("CoreTelecom", "call_operation failed: ${failure.javaClass.simpleName}")
                    if (!session.closing) CallRuntime.setStatus("系统未允许此操作，请重试。", id)
                }
            }
        }
    }
    fun answer(id: String, accepted: () -> Unit) = operate(id) { session, control ->
        val revision = session.activation.beginRequest()
        requireSuccess(control.answer(CallAttributesCompat.CALL_TYPE_AUDIO_CALL))
        checkCurrent(session)
        if (session.activation.grant(revision)) CallRuntime.onTelecomActive(id)
        accepted()
    }
    fun activate(id: String) = operate(id) { session, control ->
        if (CallRuntime.snapshot(id)?.held == true || !session.activation.active) {
            val revision = session.activation.beginRequest()
            requireSuccess(control.setActive())
            checkCurrent(session)
            if (session.activation.grant(revision)) CallRuntime.onTelecomActive(id)
        }
    }
    fun endpoints(id: String) = sessions[id]?.devices?.map { it.device() }.orEmpty()
    fun current(id: String) = sessions[id]?.current?.device()
    fun select(id: String, deviceId: String): Boolean {
        val session = sessions[id] ?: return false
        if (session.devices.none { it.identifier.toString() == deviceId }) return false
        operate(id) { current, control ->
            val endpoint = current.devices.firstOrNull { it.identifier.toString() == deviceId }
                ?: error("Endpoint removed")
            requireSuccess(control.requestEndpointChange(endpoint))
        }
        return true
    }
    internal fun disconnectCause(cause: Int) = when (cause) {
        DisconnectCause.REMOTE, DisconnectCause.REJECTED, DisconnectCause.MISSED -> cause
        else -> DisconnectCause.LOCAL
    }
    fun disconnect(id: String, cause: Int, complete: () -> Unit) {
        val session = sessions[id]
        if (session == null) { complete(); return }
        if (session.closing) {
            owner.launch { session.closed.await(); complete() }
            return
        }
        session.closing = true
        owner.launch {
            try {
                // No cancellation of addCall as a substitute for platform disconnect.
                disconnectTelecom(session.ready, cause) { session.closed.isCompleted }
            } catch (failure: Exception) {
                Log.e("CoreTelecom", "disconnect failed: ${failure.javaClass.simpleName}")
                // Keep the process owner alive; never cancel a potentially live platform session.
            } finally {
                session.observers.forEach { it.cancel() }
                complete()
            }
        }
    }
    private fun requireSuccess(result: CallControlResult) {
        if (result is CallControlResult.Error) Log.w("CoreTelecom", "operation rejected code=${result.errorCode}")
        check(result is CallControlResult.Success) { "Telecom operation rejected" }
    }
    private fun CallEndpointCompat.device(): AudioDevice = AudioDevice(identifier.toString(), name.toString(), when (type) {
        CallEndpointCompat.TYPE_SPEAKER -> CallAudioEndpoint.SPEAKER
        CallEndpointCompat.TYPE_BLUETOOTH -> CallAudioEndpoint.BLUETOOTH
        CallEndpointCompat.TYPE_WIRED_HEADSET -> CallAudioEndpoint.WIRED_HEADSET
        CallEndpointCompat.TYPE_EARPIECE -> CallAudioEndpoint.EARPIECE
        else -> CallAudioEndpoint.UNKNOWN
    })
}
