package com.mytracker.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mytracker.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Helper class for managing notifications.
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "mytracker_reminders"
        const val CHANNEL_NAME = "Goal Reminders"
        const val NOTIFICATION_ID_BASE = 1000
    }
    
    private val notificationManager = 
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for your habit tracking goals"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun sendMissedWindowNotification(goalTitle: String, windowIndex: Int) {
        val scoldMessage = getRandomScoldMessage()
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Missed window for: $goalTitle")
            .setContentText(scoldMessage)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_BASE + windowIndex, notification)
    }
    
    fun sendPraiseNotification(goalTitle: String) {
        val praiseMessage = getRandomPraiseMessage()
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(goalTitle)
            .setContentText(praiseMessage)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_BASE + 999, notification)
    }
    
    private fun getRandomScoldMessage(): String {
        val messages = context.resources.getStringArray(R.array.scold_messages)
        return messages[Random.nextInt(messages.size)]
    }
    
    private fun getRandomPraiseMessage(): String {
        val messages = context.resources.getStringArray(R.array.praise_messages)
        return messages[Random.nextInt(messages.size)]
    }
    
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}

