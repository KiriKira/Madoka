package com.callagent.rtcmedia

/** Single resource whose publication and retirement share one lifecycle lock.
 * Creation and native disposal must not hold that lock: either may block on callbacks. */
internal class CloseableSlot<T : AutoCloseable> {
    private val lock = Any()
    @Volatile var isClosed: Boolean = false; private set
    @Volatile private var value: T? = null

    fun get(): T? = value

    fun install(resource: T): Boolean {
        val accepted = synchronized(lock) {
            if (isClosed) false else {
                check(value == null)
                value = resource
                true
            }
        }
        if (!accepted) resource.close()
        return accepted
    }

    /** Returns true only for the caller that retired the slot, even if disposal throws.
     * The session must still cancel its timers, signaling worker and status in that case. */
    fun close(): Boolean {
        val retired = synchronized(lock) {
            if (isClosed) return false
            isClosed = true
            value.also { value = null }
        }
        runCatching { retired?.close() }
        return true
    }
}
