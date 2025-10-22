package com.mytracker.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mytracker.app.worker.NotificationScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Receives BOOT_COMPLETED broadcast to reschedule notifications after device reboot.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var notificationScheduler: NotificationScheduler
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            // Reschedule all notifications
            CoroutineScope(Dispatchers.IO).launch {
                notificationScheduler.rescheduleAllNotifications()
            }
        }
    }
}

