package com.mytracker.app.data.dao

import androidx.room.*
import com.mytracker.app.data.entity.PressRecordEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for PressRecord operations.
 */
@Dao
interface PressRecordDao {
    
    @Query("SELECT * FROM press_records WHERE goalId = :goalId")
    fun getPressRecordsForGoalFlow(goalId: Long): Flow<List<PressRecordEntity>>
    
    @Query("SELECT * FROM press_records WHERE goalId = :goalId")
    suspend fun getPressRecordsForGoal(goalId: Long): List<PressRecordEntity>
    
    @Query("SELECT * FROM press_records WHERE goalId = :goalId AND date = :date")
    suspend fun getPressRecordsForDate(goalId: Long, date: LocalDate): List<PressRecordEntity>
    
    @Query("SELECT * FROM press_records WHERE goalId = :goalId AND windowIndex = :windowIndex AND date = :date LIMIT 1")
    suspend fun getPressRecord(goalId: Long, windowIndex: Int, date: LocalDate): PressRecordEntity?
    
    @Query("SELECT COUNT(*) FROM press_records WHERE goalId = :goalId")
    suspend fun getTotalPressCount(goalId: Long): Int
    
    @Query("SELECT COUNT(*) FROM press_records WHERE goalId = :goalId")
    fun getTotalPressCountFlow(goalId: Long): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPressRecord(record: PressRecordEntity): Long
    
    @Delete
    suspend fun deletePressRecord(record: PressRecordEntity)
    
    @Query("DELETE FROM press_records WHERE goalId = :goalId")
    suspend fun deletePressRecordsForGoal(goalId: Long)
}

