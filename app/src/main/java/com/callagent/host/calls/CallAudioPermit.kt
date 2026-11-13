package com.callagent.host.calls

/** Independent permission sources: recovering one cannot undo another source's suspension. */
internal class CallAudioPermit {
    private var authorized = false
    private var platformActive = false
    private var ended = false
    val allowed: Boolean get() = !ended && authorized && platformActive
    fun authorize(value: Boolean) { authorized = value }
    fun activate(value: Boolean) { platformActive = value }
    fun end() { ended = true }
}
