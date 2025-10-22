package com.mytracker.app.data.repository

import com.mytracker.app.data.dao.GoalDao
import com.mytracker.app.data.dao.PressRecordDao
import com.mytracker.app.data.entity.GoalEntity
import com.mytracker.app.data.entity.PressRecordEntity
import com.mytracker.app.data.entity.TimeWindow
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/**
 * Unit tests for GoalRepository.
 * Tests the business logic of goal and press record management.
 */
class GoalRepositoryTest {
    
    private lateinit var repository: GoalRepository
    private lateinit var goalDao: GoalDao
    private lateinit var pressRecordDao: PressRecordDao
    
    @Before
    fun setup() {
        goalDao = mockk(relaxed = true)
        pressRecordDao = mockk(relaxed = true)
        repository = GoalRepository(goalDao, pressRecordDao)
    }
    
    @Test
    fun `createGoal creates goal with correct parameters`() = runTest {
        // Given
        val title = "Test Goal"
        val durationDays = 7
        val timeWindows = listOf(
            TimeWindow(0, 9, 0, 10, 0),
            TimeWindow(1, 14, 0, 15, 0)
        )
        coEvery { goalDao.insertGoal(any()) } returns 1L
        
        // When
        val goalId = repository.createGoal(title, durationDays, timeWindows)
        
        // Then
        assertEquals(1L, goalId)
        coVerify {
            goalDao.insertGoal(match { goal ->
                goal.title == title &&
                goal.durationDays == durationDays &&
                goal.timeWindows.size == 2 &&
                goal.isActive
            })
        }
    }
    
    @Test
    fun `recordPress inserts new press record successfully`() = runTest {
        // Given
        val goalId = 1L
        val windowIndex = 0
        val date = LocalDate.now()
        coEvery { pressRecordDao.getPressRecord(goalId, windowIndex, date) } returns null
        coEvery { pressRecordDao.insertPressRecord(any()) } returns 1L
        
        // When
        val success = repository.recordPress(goalId, windowIndex, date)
        
        // Then
        assertTrue(success)
        coVerify { pressRecordDao.insertPressRecord(any()) }
    }
    
    @Test
    fun `recordPress returns false when press already exists`() = runTest {
        // Given
        val goalId = 1L
        val windowIndex = 0
        val date = LocalDate.now()
        val existingPress = PressRecordEntity(
            id = 1,
            goalId = goalId,
            windowIndex = windowIndex,
            date = date
        )
        coEvery { pressRecordDao.getPressRecord(goalId, windowIndex, date) } returns existingPress
        
        // When
        val success = repository.recordPress(goalId, windowIndex, date)
        
        // Then
        assertFalse(success)
        coVerify(exactly = 0) { pressRecordDao.insertPressRecord(any()) }
    }
    
    @Test
    fun `hasPressed returns true when press exists`() = runTest {
        // Given
        val goalId = 1L
        val windowIndex = 0
        val date = LocalDate.now()
        val existingPress = PressRecordEntity(
            id = 1,
            goalId = goalId,
            windowIndex = windowIndex,
            date = date
        )
        coEvery { pressRecordDao.getPressRecord(goalId, windowIndex, date) } returns existingPress
        
        // When
        val hasPressed = repository.hasPressed(goalId, windowIndex, date)
        
        // Then
        assertTrue(hasPressed)
    }
    
    @Test
    fun `hasPressed returns false when press does not exist`() = runTest {
        // Given
        val goalId = 1L
        val windowIndex = 0
        val date = LocalDate.now()
        coEvery { pressRecordDao.getPressRecord(goalId, windowIndex, date) } returns null
        
        // When
        val hasPressed = repository.hasPressed(goalId, windowIndex, date)
        
        // Then
        assertFalse(hasPressed)
    }
    
    @Test
    fun `deleteGoal deletes press records and goal`() = runTest {
        // Given
        val goalId = 1L
        
        // When
        repository.deleteGoal(goalId)
        
        // Then
        coVerify { pressRecordDao.deletePressRecordsForGoal(goalId) }
        coVerify { goalDao.deleteGoalById(goalId) }
    }
}


