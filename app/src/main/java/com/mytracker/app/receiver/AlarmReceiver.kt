package com.mytracker.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mytracker.app.data.repository.GoalRepository
import com.mytracker.app.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * Receiver for alarm-based notifications.
 * Triggers when a time window ends to check if user pressed the button.
 */
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var repository: GoalRepository
    
    @Inject
    lateinit var notificationHelper: NotificationHelper
    
    companion object {
        const val EXTRA_GOAL_ID = "goal_id"
        const val EXTRA_WINDOW_INDEX = "window_index"
        const val EXTRA_DATE = "date"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val goalId = intent.getLongExtra(EXTRA_GOAL_ID, -1L)
        val windowIndex = intent.getIntExtra(EXTRA_WINDOW_INDEX, -1)
        val dateString = intent.getStringExtra(EXTRA_DATE) ?: return
        
        if (goalId == -1L || windowIndex == -1) return
        
        // Use goAsync() to allow coroutine execution
        val pendingResult = goAsync()
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val date = LocalDate.parse(dateString)
                
                // Check if the goal still exists
                val goal = repository.getActiveGoal()
                if (goal == null || goal.id != goalId) {
                    pendingResult.finish()
                    return@launch
                }
                
                // Check if user pressed for this window
                val hasPressed = repository.hasPressed(goalId, windowIndex, date)
                
                if (!hasPressed) {
                    // User missed this window, send notification
                    notificationHelper.sendMissedWindowNotification(goal.title, windowIndex)
                }
                
                pendingResult.finish()
            } catch (e: Exception) {
                pendingResult.finish()
            }
        }
    }
}

