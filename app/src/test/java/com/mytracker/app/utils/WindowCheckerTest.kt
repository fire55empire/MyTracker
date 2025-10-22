package com.mytracker.app.utils

import com.mytracker.app.data.entity.TimeWindow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Unit tests for WindowChecker.
 */
class WindowCheckerTest {
    
    private lateinit var windowChecker: WindowChecker
    private lateinit var fakeTimeProvider: FakeTimeProvider
    
    @Before
    fun setup() {
        fakeTimeProvider = FakeTimeProvider()
        windowChecker = WindowChecker(fakeTimeProvider)
    }
    
    @Test
    fun `getCurrentWindowIndex returns null when time is before all windows`() {
        fakeTimeProvider.currentTime = LocalTime.of(8, 0)
        val windows = listOf(
            TimeWindow(0, 9, 0, 10, 0),
            TimeWindow(1, 14, 0, 15, 0)
        )
        assertNull(windowChecker.getCurrentWindowIndex(windows))
    }
    
    @Test
    fun `getCurrentWindowIndex returns null when time is after all windows`() {
        fakeTimeProvider.currentTime = LocalTime.of(16, 0)
        val windows = listOf(
            TimeWindow(0, 9, 0, 10, 0),
            TimeWindow(1, 14, 0, 15, 0)
        )
        assertNull(windowChecker.getCurrentWindowIndex(windows))
    }
    
    @Test
    fun `getCurrentWindowIndex returns null when time is between windows`() {
        fakeTimeProvider.currentTime = LocalTime.of(12, 0)
        val windows = listOf(
            TimeWindow(0, 9, 0, 10, 0),
            TimeWindow(1, 14, 0, 15, 0)
        )
        assertNull(windowChecker.getCurrentWindowIndex(windows))
    }
    
    @Test
    fun `getCurrentWindowIndex returns correct index when in first window`() {
        fakeTimeProvider.currentTime = LocalTime.of(9, 30)
        val windows = listOf(
            TimeWindow(0, 9, 0, 10, 0),
            TimeWindow(1, 14, 0, 15, 0)
        )
        assertEquals(0, windowChecker.getCurrentWindowIndex(windows))
    }
    
    @Test
    fun `getCurrentWindowIndex returns correct index when in second window`() {
        fakeTimeProvider.currentTime = LocalTime.of(14, 30)
        val windows = listOf(
            TimeWindow(0, 9, 0, 10, 0),
            TimeWindow(1, 14, 0, 15, 0)
        )
        assertEquals(1, windowChecker.getCurrentWindowIndex(windows))
    }
    
    @Test
    fun `isTimeInWindow returns true when time is at start`() {
        val time = LocalTime.of(9, 0)
        val window = TimeWindow(0, 9, 0, 10, 0)
        assertTrue(windowChecker.isTimeInWindow(time, window))
    }
    
    @Test
    fun `isTimeInWindow returns false when time is at end`() {
        val time = LocalTime.of(10, 0)
        val window = TimeWindow(0, 9, 0, 10, 0)
        assertFalse(windowChecker.isTimeInWindow(time, window))
    }
    
    @Test
    fun `isTimeInWindow returns true when time is in middle`() {
        val time = LocalTime.of(9, 30)
        val window = TimeWindow(0, 9, 0, 10, 0)
        assertTrue(windowChecker.isTimeInWindow(time, window))
    }
    
    @Test
    fun `isInAnyWindow returns true when in a window`() {
        fakeTimeProvider.currentTime = LocalTime.of(9, 30)
        val windows = listOf(
            TimeWindow(0, 9, 0, 10, 0),
            TimeWindow(1, 14, 0, 15, 0)
        )
        assertTrue(windowChecker.isInAnyWindow(windows))
    }
    
    @Test
    fun `isInAnyWindow returns false when not in a window`() {
        fakeTimeProvider.currentTime = LocalTime.of(12, 0)
        val windows = listOf(
            TimeWindow(0, 9, 0, 10, 0),
            TimeWindow(1, 14, 0, 15, 0)
        )
        assertFalse(windowChecker.isInAnyWindow(windows))
    }
}

/**
 * Fake TimeProvider for testing.
 */
class FakeTimeProvider : TimeProvider {
    var currentDate: LocalDate = LocalDate.now()
    var currentTime: LocalTime = LocalTime.now()
    
    override fun currentDate(): LocalDate = currentDate
    override fun currentTime(): LocalTime = currentTime
    override fun currentDateTime(): LocalDateTime = LocalDateTime.of(currentDate, currentTime)
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}

