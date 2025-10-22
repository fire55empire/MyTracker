package com.mytracker.app.data.repository

import com.mytracker.app.data.dao.GoalDao
import com.mytracker.app.data.dao.PressRecordDao
import com.mytracker.app.data.entity.GoalEntity
import com.mytracker.app.data.entity.PressRecordEntity
import com.mytracker.app.data.entity.TimeWindow
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing goals and press records.
 * Provides a clean API for the domain/UI layers.
 */
@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao,
    private val pressRecordDao: PressRecordDao
) {
    
    // Goal operations
    fun getActiveGoalFlow(): Flow<GoalEntity?> = goalDao.getActiveGoalFlow()
    
    suspend fun getActiveGoal(): GoalEntity? = goalDao.getActiveGoal()
    
    suspend fun createGoal(
        title: String,
        durationDays: Int,
        timeWindows: List<TimeWindow>
    ): Long {
        val goal = GoalEntity(
            title = title,
            startDate = LocalDate.now(),
            durationDays = durationDays,
            timeWindows = timeWindows,
            isActive = true
        )
        return goalDao.insertGoal(goal)
    }
    
    suspend fun deleteGoal(goalId: Long) {
        // First delete all press records (cascade should handle this, but being explicit)
        pressRecordDao.deletePressRecordsForGoal(goalId)
        goalDao.deleteGoalById(goalId)
    }
    
    // Press record operations
    fun getPressRecordsFlow(goalId: Long): Flow<List<PressRecordEntity>> =
        pressRecordDao.getPressRecordsForGoalFlow(goalId)
    
    fun getTotalPressCountFlow(goalId: Long): Flow<Int> =
        pressRecordDao.getTotalPressCountFlow(goalId)
    
    suspend fun getTotalPressCount(goalId: Long): Int =
        pressRecordDao.getTotalPressCount(goalId)
    
    suspend fun getPressRecordsForDate(goalId: Long, date: LocalDate): List<PressRecordEntity> =
        pressRecordDao.getPressRecordsForDate(goalId, date)
    
    suspend fun recordPress(goalId: Long, windowIndex: Int, date: LocalDate): Boolean {
        // Check if press already exists for this window and date
        val existing = pressRecordDao.getPressRecord(goalId, windowIndex, date)
        if (existing != null) {
            return false // Already pressed
        }
        
        val record = PressRecordEntity(
            goalId = goalId,
            windowIndex = windowIndex,
            date = date
        )
        val insertedId = pressRecordDao.insertPressRecord(record)
        return insertedId > 0
    }
    
    suspend fun hasPressed(goalId: Long, windowIndex: Int, date: LocalDate): Boolean {
        return pressRecordDao.getPressRecord(goalId, windowIndex, date) != null
    }
}

