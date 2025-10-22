package com.mytracker.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mytracker.app.data.repository.GoalRepository
import com.mytracker.app.utils.NotificationHelper
import com.mytracker.app.utils.TimeProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

/**
 * Worker that checks if a window was missed and sends a notification.
 */
@HiltWorker
class WindowCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: GoalRepository,
    private val notificationHelper: NotificationHelper,
    private val timeProvider: TimeProvider
) : CoroutineWorker(appContext, workerParams) {
    
    companion object {
        const val KEY_GOAL_ID = "goal_id"
        const val KEY_WINDOW_INDEX = "window_index"
        const val KEY_DATE = "date"
        const val WORK_NAME_PREFIX = "window_check_"
    }
    
    override suspend fun doWork(): Result {
        return try {
            val goalId = inputData.getLong(KEY_GOAL_ID, -1L)
            val windowIndex = inputData.getInt(KEY_WINDOW_INDEX, -1)
            val dateString = inputData.getString(KEY_DATE) ?: return Result.failure()
            val date = LocalDate.parse(dateString)
            
            if (goalId == -1L || windowIndex == -1) {
                return Result.failure()
            }
            
            // Check if the goal still exists
            val goal = repository.getActiveGoal()
            if (goal == null || goal.id != goalId) {
                // Goal was deleted or cancelled
                return Result.success()
            }
            
            // Check if user pressed for this window
            val hasPressed = repository.hasPressed(goalId, windowIndex, date)
            
            if (!hasPressed) {
                // User missed this window, send notification
                notificationHelper.sendMissedWindowNotification(goal.title, windowIndex)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

