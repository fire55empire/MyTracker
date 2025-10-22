package com.mytracker.app.worker

import android.content.Context
import androidx.work.*
import com.mytracker.app.data.repository.GoalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages scheduling of notifications using WorkManager.
 */
@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: GoalRepository
) {
    
    private val workManager = WorkManager.getInstance(context)
    
    /**
     * Schedules all notifications for a goal.
     * This should be called when a goal is created or after device reboot.
     */
    suspend fun scheduleNotifications(goalId: Long) {
        val goal = repository.getActiveGoal() ?: return
        if (goal.id != goalId) return
        
        val today = LocalDate.now()
        val endDate = goal.startDate.plusDays(goal.durationDays.toLong())
        
        // Schedule for each day and each window
        var currentDate = today
        while (!currentDate.isAfter(endDate)) {
            goal.timeWindows.forEach { window ->
                scheduleWindowCheck(goalId, window.index, currentDate, window.endHour, window.endMinute)
            }
            currentDate = currentDate.plusDays(1)
        }
    }
    
    /**
     * Schedules a check for a specific window on a specific date.
     */
    private fun scheduleWindowCheck(
        goalId: Long,
        windowIndex: Int,
        date: LocalDate,
        endHour: Int,
        endMinute: Int
    ) {
        val endTime = LocalDateTime.of(date, LocalTime.of(endHour, endMinute))
        val now = LocalDateTime.now()
        
        // Only schedule if the end time is in the future
        if (endTime.isBefore(now)) {
            return
        }
        
        val delay = Duration.between(now, endTime).toMillis()
        
        val inputData = Data.Builder()
            .putLong(WindowCheckWorker.KEY_GOAL_ID, goalId)
            .putInt(WindowCheckWorker.KEY_WINDOW_INDEX, windowIndex)
            .putString(WindowCheckWorker.KEY_DATE, date.toString())
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<WindowCheckWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(getWorkTag(goalId))
            .build()
        
        workManager.enqueue(workRequest)
    }
    
    /**
     * Cancels all notifications for a goal.
     */
    fun cancelNotifications(goalId: Long) {
        workManager.cancelAllWorkByTag(getWorkTag(goalId))
    }
    
    /**
     * Reschedules all notifications (e.g., after reboot).
     */
    suspend fun rescheduleAllNotifications() {
        val goal = repository.getActiveGoal()
        if (goal != null) {
            // Cancel existing work first
            cancelNotifications(goal.id)
            // Reschedule
            scheduleNotifications(goal.id)
        }
    }
    
    private fun getWorkTag(goalId: Long): String {
        return "${WindowCheckWorker.WORK_NAME_PREFIX}$goalId"
    }
}

