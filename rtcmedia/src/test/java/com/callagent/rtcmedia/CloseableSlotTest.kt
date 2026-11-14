package com.callagent.rtcmedia

import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/** Regression tests authored for CI; not compiled or run in the toolchain-free repair workspace. */
class CloseableSlotTest {
    @Test fun closeAfterConstructionButBeforePublicationClosesLatePeerExactlyOnce() {
        val slot = CloseableSlot<AutoCloseable>()
        val created = CountDownLatch(1)
        val publish = CountDownLatch(1)
        val closes = AtomicInteger()
        val worker = Executors.newSingleThreadExecutor()
        try {
            val installed = worker.submit<Boolean> {
                val native = AutoCloseable { closes.incrementAndGet() }
                // The resource already exists; publication is deliberately blocked.
                created.countDown()
                check(publish.await(5, TimeUnit.SECONDS))
                slot.install(native)
            }
            assertTrue(created.await(5, TimeUnit.SECONDS))
            assertTrue(slot.close())
            assertTrue(slot.isClosed)
            assertNull(slot.get())
            assertEquals(0, closes.get())
            publish.countDown()
            assertFalse(installed.get(5, TimeUnit.SECONDS))
            assertFalse(slot.close())
            assertNull(slot.get())
            assertEquals(1, closes.get())
        } finally { publish.countDown(); worker.shutdownNow() }
    }

    @Test fun installedPeerIsDetachedAndClosedExactlyOnce() {
        val slot = CloseableSlot<AutoCloseable>()
        val closes = AtomicInteger()
        val native = AutoCloseable {
            assertTrue(slot.isClosed)
            assertNull(slot.get())
            closes.incrementAndGet()
        }
        assertTrue(slot.install(native))
        assertSame(native, slot.get())
        assertTrue(slot.close())
        assertFalse(slot.close())
        assertEquals(1, closes.get())
    }

    @Test fun blockedNativeDisposalDoesNotHoldLifecycleLock() {
        val slot = CloseableSlot<AutoCloseable>()
        val disposing = CountDownLatch(1)
        val finish = CountDownLatch(1)
        val closes = AtomicInteger()
        val workers = Executors.newFixedThreadPool(2)
        try {
            assertTrue(slot.install(AutoCloseable {
                disposing.countDown()
                check(finish.await(5, TimeUnit.SECONDS))
            }))
            val closed = workers.submit<Boolean> { slot.close() }
            assertTrue(disposing.await(5, TimeUnit.SECONDS))
            // This install needs the lifecycle lock and must not wait for native.close().
            val late = workers.submit<Boolean> { slot.install(AutoCloseable { closes.incrementAndGet() }) }
            assertFalse(late.get(5, TimeUnit.SECONDS))
            assertEquals(1, closes.get())
            finish.countDown()
            assertTrue(closed.get(5, TimeUnit.SECONDS))
        } finally { finish.countDown(); workers.shutdownNow() }
    }

    @Test fun disposalFailureStillRetiresSlotAndLetsSessionFinishCleanup() {
        val slot = CloseableSlot<AutoCloseable>()
        assertTrue(slot.install(AutoCloseable { error("native close failure") }))
        assertTrue(slot.close())
        assertTrue(slot.isClosed)
        assertNull(slot.get())
        assertFalse(slot.close())
    }
}
