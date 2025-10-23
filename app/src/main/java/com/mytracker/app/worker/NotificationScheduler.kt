package com.mytracker.app.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.mytracker.app.data.repository.GoalRepository
import com.mytracker.app.receiver.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages scheduling of exact alarm notifications using AlarmManager.
 */
@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: GoalRepository
) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
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
     * Schedules an exact alarm for a specific window on a specific date.
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
        if (endTime.isBefore(now)) return
        
        val triggerTime = endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_GOAL_ID, goalId)
            putExtra(AlarmReceiver.EXTRA_WINDOW_INDEX, windowIndex)
            putExtra(AlarmReceiver.EXTRA_DATE, date.toString())
        }
        
        val requestCode = getRequestCode(goalId, windowIndex, date)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Use exact alarm for precise notification timing
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }
    
    /**
     * Cancels all notifications for a goal.
     */
    fun cancelNotifications(goalId: Long) {
        // Cancel all possible alarms for this goal
        // We need to iterate through possible dates and windows
        // For simplicity, we'll cancel based on a pattern
        for (dayOffset in 0..365) {
            for (windowIndex in 0..10) {
                val date = LocalDate.now().plusDays(dayOffset.toLong())
                val requestCode = getRequestCode(goalId, windowIndex, date)
                
                val intent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                
                pendingIntent?.let {
                    alarmManager.cancel(it)
                    it.cancel()
                }
            }
        }
    }
    
    /**
     * Reschedules all notifications (e.g., after reboot).
     */
    suspend fun rescheduleAllNotifications() {
        val goal = repository.getActiveGoal()
        if (goal != null) {
            // Cancel existing alarms first
            cancelNotifications(goal.id)
            // Reschedule
            scheduleNotifications(goal.id)
        }
    }
    
    /**
     * Generates a unique request code for each alarm.
     */
    private fun getRequestCode(goalId: Long, windowIndex: Int, date: LocalDate): Int {
        // Combine goalId, windowIndex, and date to create a unique code
        val dateHash = date.toString().hashCode()
        return (goalId.toInt() * 1000 + windowIndex * 100 + (dateHash % 100))
    }
}

