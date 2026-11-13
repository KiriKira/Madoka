package com.callagent.host.calls

/** A delayed operation result cannot override a newer platform hold/activation callback. */
internal class PlatformActivation {
    private var revision = 0L
    @Volatile var active = false
        private set
    @Synchronized fun beginRequest(): Long = revision
    @Synchronized fun observe(active: Boolean) {
        revision++
        this.active = active
    }
    @Synchronized fun grant(requestRevision: Long): Boolean {
        if (requestRevision != revision) return false
        revision++
        active = true
        return true
    }
}
