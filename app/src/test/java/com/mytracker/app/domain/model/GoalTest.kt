package com.mytracker.app.domain.model

import com.mytracker.app.data.entity.TimeWindow
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

/**
 * Unit tests for Goal domain model.
 */
class GoalTest {
    
    @Test
    fun `calculateProgress with no presses returns 0`() {
        val goal = createTestGoal(durationDays = 7, windowsPerDay = 2)
        val progress = goal.calculateProgress(0)
        assertEquals(0f, progress, 0.001f)
    }
    
    @Test
    fun `calculateProgress with half presses returns 0_5`() {
        val goal = createTestGoal(durationDays = 10, windowsPerDay = 2)
        // Total required = 10 * 2 = 20
        // Half = 10
        val progress = goal.calculateProgress(10)
        assertEquals(0.5f, progress, 0.001f)
    }
    
    @Test
    fun `calculateProgress with all presses returns 1`() {
        val goal = createTestGoal(durationDays = 5, windowsPerDay = 3)
        // Total required = 5 * 3 = 15
        val progress = goal.calculateProgress(15)
        assertEquals(1f, progress, 0.001f)
    }
    
    @Test
    fun `calculateProgress with more than required presses returns 1`() {
        val goal = createTestGoal(durationDays = 5, windowsPerDay = 2)
        // Total required = 5 * 2 = 10
        val progress = goal.calculateProgress(20)
        assertEquals(1f, progress, 0.001f)
    }
    
    @Test
    fun `calculateProgressPercentage formats correctly`() {
        val goal = createTestGoal(durationDays = 10, windowsPerDay = 2)
        // Total required = 20
        val percentage = goal.calculateProgressPercentage(5)
        assertEquals("25.0%", percentage)
    }
    
    @Test
    fun `isCompleted returns false when not all presses recorded`() {
        val goal = createTestGoal(durationDays = 7, windowsPerDay = 2)
        // Total required = 14
        assertFalse(goal.isCompleted(10))
    }
    
    @Test
    fun `isCompleted returns true when all presses recorded`() {
        val goal = createTestGoal(durationDays = 7, windowsPerDay = 2)
        // Total required = 14
        assertTrue(goal.isCompleted(14))
    }
    
    @Test
    fun `totalRequiredPresses calculated correctly`() {
        val goal = createTestGoal(durationDays = 7, windowsPerDay = 3)
        assertEquals(21, goal.totalRequiredPresses)
    }
    
    @Test
    fun `endDate calculated correctly`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val goal = Goal(
            id = 1,
            title = "Test",
            startDate = startDate,
            durationDays = 7,
            timeWindows = emptyList()
        )
        assertEquals(LocalDate.of(2024, 1, 7), goal.endDate)
    }
    
    private fun createTestGoal(durationDays: Int, windowsPerDay: Int): Goal {
        val windows = (0 until windowsPerDay).map { index ->
            TimeWindow(
                index = index,
                startHour = 9 + index * 2,
                startMinute = 0,
                endHour = 10 + index * 2,
                endMinute = 0
            )
        }
        return Goal(
            id = 1,
            title = "Test Goal",
            startDate = LocalDate.now(),
            durationDays = durationDays,
            timeWindows = windows
        )
    }
}

