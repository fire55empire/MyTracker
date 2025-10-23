package com.mytracker.app

import com.mytracker.app.data.entity.TimeWindow
import com.mytracker.app.domain.model.Goal
import com.mytracker.app.domain.validation.GoalValidator
import com.mytracker.app.domain.validation.ValidationResult
import com.mytracker.app.utils.FakeTimeProvider
import com.mytracker.app.utils.WindowChecker
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

/**
 * Integration tests that verify end-to-end scenarios.
 */
class IntegrationTest {
    
    @Test
    fun `complete goal creation and validation flow`() {
        // Given
        val title = "Exercise Daily"
        val durationDays = 30
        val windows = listOf(
            TimeWindow(0, 8, 0, 10, 0),
            TimeWindow(1, 18, 0, 20, 0)
        )
        
        // When - Validate
        val validationResult = GoalValidator.validateGoal(title, durationDays, windows)
        
        // Then
        assertTrue(validationResult is ValidationResult.Success)
        
        // When - Create goal domain model
        val goal = Goal(
            id = 1,
            title = title,
            startDate = LocalDate.now(),
            durationDays = durationDays,
            timeWindows = windows
        )
        
        // Then - Verify calculations
        assertEquals(60, goal.totalRequiredPresses) // 30 days * 2 windows
        assertEquals(0f, goal.calculateProgress(0), 0.001f)
        assertEquals(0.5f, goal.calculateProgress(30), 0.001f)
        assertEquals(1f, goal.calculateProgress(60), 0.001f)
    }
    
    @Test
    fun `window checking during different times of day`() {
        // Given
        val timeProvider = FakeTimeProvider()
        val windowChecker = WindowChecker(timeProvider)
        val windows = listOf(
            TimeWindow(0, 8, 0, 10, 0),
            TimeWindow(1, 12, 0, 14, 0),
            TimeWindow(2, 18, 0, 20, 0)
        )
        
        // Test early morning (before any window)
        timeProvider.currentTime = LocalTime.of(7, 0)
        assertNull(windowChecker.getCurrentWindowIndex(windows))
        
        // Test during first window
        timeProvider.currentTime = LocalTime.of(9, 0)
        assertEquals(0, windowChecker.getCurrentWindowIndex(windows))
        
        // Test between windows
        timeProvider.currentTime = LocalTime.of(11, 0)
        assertNull(windowChecker.getCurrentWindowIndex(windows))
        
        // Test during second window
        timeProvider.currentTime = LocalTime.of(13, 0)
        assertEquals(1, windowChecker.getCurrentWindowIndex(windows))
        
        // Test during third window
        timeProvider.currentTime = LocalTime.of(19, 0)
        assertEquals(2, windowChecker.getCurrentWindowIndex(windows))
        
        // Test late night (after all windows)
        timeProvider.currentTime = LocalTime.of(22, 0)
        assertNull(windowChecker.getCurrentWindowIndex(windows))
    }
    
    @Test
    fun `progress calculation for typical 7-day goal with 2 windows`() {
        // Given - 7-day goal with morning and evening windows
        val goal = Goal(
            id = 1,
            title = "Daily Routine",
            startDate = LocalDate.now(),
            durationDays = 7,
            timeWindows = listOf(
                TimeWindow(0, 7, 0, 9, 0),   // Morning
                TimeWindow(1, 19, 0, 21, 0)  // Evening
            )
        )
        
        // Total required: 7 days × 2 windows = 14 presses
        assertEquals(14, goal.totalRequiredPresses)
        
        // Day 1: completed both windows (2 presses)
        assertEquals("14.3%", goal.calculateProgressPercentage(2))
        assertEquals(0.143f, goal.calculateProgress(2), 0.01f)
        
        // Day 3: completed 6 presses
        assertEquals("42.9%", goal.calculateProgressPercentage(6))
        
        // Day 5: completed 10 presses
        assertEquals("71.4%", goal.calculateProgressPercentage(10))
        
        // Day 7: completed all 14 presses
        assertEquals("100.0%", goal.calculateProgressPercentage(14))
        assertTrue(goal.isCompleted(14))
    }
    
    @Test
    fun `window overlap detection prevents invalid configurations`() {
        // Valid non-overlapping windows
        val validWindows = listOf(
            TimeWindow(0, 8, 0, 10, 0),
            TimeWindow(1, 10, 0, 12, 0),
            TimeWindow(2, 14, 0, 16, 0)
        )
        val validResult = GoalValidator.validateTimeWindows(validWindows)
        assertTrue(validResult is ValidationResult.Success)
        
        // Invalid overlapping windows
        val overlappingWindows = listOf(
            TimeWindow(0, 8, 0, 11, 0),
            TimeWindow(1, 10, 0, 12, 0)  // Overlaps with previous
        )
        val invalidResult = GoalValidator.validateTimeWindows(overlappingWindows)
        assertTrue(invalidResult is ValidationResult.Error)
        assertTrue((invalidResult as ValidationResult.Error).message.contains("overlap"))
    }
    
    @Test
    fun `midnight crossing validation prevents invalid windows`() {
        // Invalid window crossing midnight
        val midnightWindow = listOf(
            TimeWindow(0, 23, 0, 1, 0)  // Crosses midnight
        )
        val result = GoalValidator.validateTimeWindows(midnightWindow)
        assertTrue(result is ValidationResult.Error)
        
        // Valid window at end of day
        val validEveningWindow = listOf(
            TimeWindow(0, 22, 0, 23, 59)  // Doesn't cross midnight
        )
        val validResult = GoalValidator.validateTimeWindows(validEveningWindow)
        assertTrue(validResult is ValidationResult.Success)
    }
    
    @Test
    fun `edge case - single day goal with multiple windows`() {
        val goal = Goal(
            id = 1,
            title = "One Day Challenge",
            startDate = LocalDate.now(),
            durationDays = 1,
            timeWindows = listOf(
                TimeWindow(0, 8, 0, 9, 0),
                TimeWindow(1, 12, 0, 13, 0),
                TimeWindow(2, 18, 0, 19, 0)
            )
        )
        
        assertEquals(3, goal.totalRequiredPresses)
        assertEquals("0.0%", goal.calculateProgressPercentage(0))
        assertEquals("33.3%", goal.calculateProgressPercentage(1))
        assertEquals("66.7%", goal.calculateProgressPercentage(2))
        assertEquals("100.0%", goal.calculateProgressPercentage(3))
    }
    
    @Test
    fun `edge case - long term goal (365 days)`() {
        val goal = Goal(
            id = 1,
            title = "Year Long Habit",
            startDate = LocalDate.of(2024, 1, 1),
            durationDays = 365,
            timeWindows = listOf(
                TimeWindow(0, 9, 0, 10, 0)
            )
        )
        
        assertEquals(365, goal.totalRequiredPresses)
        // Goal starts Jan 1, runs for 365 days, ends on day 365 which is Dec 30
        // (startDate + durationDays - 1 = Jan 1 + 364 days = Dec 30, 2024 is leap year)
        assertEquals(LocalDate.of(2024, 12, 30), goal.endDate)
        
        // After 100 days
        assertEquals("27.4%", goal.calculateProgressPercentage(100))
        
        // After 6 months (182 days)
        assertEquals("49.9%", goal.calculateProgressPercentage(182))
    }
}


