package com.mytracker.app.domain.validation

import com.mytracker.app.data.entity.TimeWindow
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for GoalValidator.
 */
class GoalValidatorTest {
    
    @Test
    fun `validateTitle with empty title returns error`() {
        val result = GoalValidator.validateTitle("")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Title cannot be empty", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateTitle with blank title returns error`() {
        val result = GoalValidator.validateTitle("   ")
        assertTrue(result is ValidationResult.Error)
    }
    
    @Test
    fun `validateTitle with valid title returns success`() {
        val result = GoalValidator.validateTitle("My Goal")
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateTitle with too long title returns error`() {
        val longTitle = "a".repeat(101)
        val result = GoalValidator.validateTitle(longTitle)
        assertTrue(result is ValidationResult.Error)
    }
    
    @Test
    fun `validateDurationDays with zero returns error`() {
        val result = GoalValidator.validateDurationDays(0)
        assertTrue(result is ValidationResult.Error)
    }
    
    @Test
    fun `validateDurationDays with negative returns error`() {
        val result = GoalValidator.validateDurationDays(-5)
        assertTrue(result is ValidationResult.Error)
    }
    
    @Test
    fun `validateDurationDays with valid number returns success`() {
        val result = GoalValidator.validateDurationDays(7)
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateTimeWindows with empty list returns error`() {
        val result = GoalValidator.validateTimeWindows(emptyList())
        assertTrue(result is ValidationResult.Error)
        assertEquals("At least one time window is required", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateTimeWindows with valid window returns success`() {
        val window = TimeWindow(0, 9, 0, 10, 0)
        val result = GoalValidator.validateTimeWindows(listOf(window))
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateTimeWindows with invalid window crossing midnight returns error`() {
        val window = TimeWindow(0, 23, 0, 1, 0) // 23:00 to 01:00
        val result = GoalValidator.validateTimeWindows(listOf(window))
        assertTrue(result is ValidationResult.Error)
    }
    
    @Test
    fun `validateTimeWindows with overlapping windows returns error`() {
        val window1 = TimeWindow(0, 9, 0, 11, 0)
        val window2 = TimeWindow(1, 10, 0, 12, 0) // Overlaps with window1
        val result = GoalValidator.validateTimeWindows(listOf(window1, window2))
        assertTrue(result is ValidationResult.Error)
        assertTrue((result as ValidationResult.Error).message.contains("overlap"))
    }
    
    @Test
    fun `validateTimeWindows with non-overlapping windows returns success`() {
        val window1 = TimeWindow(0, 9, 0, 10, 0)
        val window2 = TimeWindow(1, 14, 0, 15, 0)
        val result = GoalValidator.validateTimeWindows(listOf(window1, window2))
        assertTrue(result is ValidationResult.Success)
    }
}

