package com.mytracker.app.data.entity

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for TimeWindow.
 */
class TimeWindowTest {
    
    @Test
    fun `isValid returns true for valid window`() {
        val window = TimeWindow(0, 9, 0, 10, 0)
        assertTrue(window.isValid())
    }
    
    @Test
    fun `isValid returns false when start equals end`() {
        val window = TimeWindow(0, 9, 0, 9, 0)
        assertFalse(window.isValid())
    }
    
    @Test
    fun `isValid returns false when end is before start`() {
        val window = TimeWindow(0, 10, 0, 9, 0)
        assertFalse(window.isValid())
    }
    
    @Test
    fun `isValid returns false when hour is out of range`() {
        val window = TimeWindow(0, 24, 0, 25, 0)
        assertFalse(window.isValid())
    }
    
    @Test
    fun `isValid returns false when minute is out of range`() {
        val window = TimeWindow(0, 9, 60, 10, 0)
        assertFalse(window.isValid())
    }
    
    @Test
    fun `overlaps returns true for overlapping windows`() {
        val window1 = TimeWindow(0, 9, 0, 11, 0)
        val window2 = TimeWindow(1, 10, 0, 12, 0)
        assertTrue(window1.overlaps(window2))
        assertTrue(window2.overlaps(window1))
    }
    
    @Test
    fun `overlaps returns false for non-overlapping windows`() {
        val window1 = TimeWindow(0, 9, 0, 10, 0)
        val window2 = TimeWindow(1, 10, 0, 11, 0)
        assertFalse(window1.overlaps(window2))
        assertFalse(window2.overlaps(window1))
    }
    
    @Test
    fun `overlaps returns false for adjacent windows`() {
        val window1 = TimeWindow(0, 9, 0, 10, 0)
        val window2 = TimeWindow(1, 10, 0, 11, 0)
        assertFalse(window1.overlaps(window2))
    }
    
    @Test
    fun `overlaps returns true for contained window`() {
        val window1 = TimeWindow(0, 9, 0, 12, 0)
        val window2 = TimeWindow(1, 10, 0, 11, 0)
        assertTrue(window1.overlaps(window2))
        assertTrue(window2.overlaps(window1))
    }
    
    @Test
    fun `toDisplayString formats correctly`() {
        val window = TimeWindow(0, 9, 30, 10, 45)
        assertEquals("09:30 - 10:45", window.toDisplayString())
    }
}

