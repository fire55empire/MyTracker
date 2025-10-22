package com.mytracker.app.data.dao

import androidx.room.*
import com.mytracker.app.data.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Goal operations.
 */
@Dao
interface GoalDao {
    
    @Query("SELECT * FROM goals WHERE isActive = 1 LIMIT 1")
    fun getActiveGoalFlow(): Flow<GoalEntity?>
    
    @Query("SELECT * FROM goals WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveGoal(): GoalEntity?
    
    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: Long): GoalEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long
    
    @Update
    suspend fun updateGoal(goal: GoalEntity)
    
    @Delete
    suspend fun deleteGoal(goal: GoalEntity)
    
    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Long)
    
    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()
}

