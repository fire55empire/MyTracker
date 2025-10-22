package com.mytracker.app.utils

import com.mytracker.app.data.entity.TimeWindow
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility to check if current time is within a time window.
 */
@Singleton
class WindowChecker @Inject constructor(
    private val timeProvider: TimeProvider
) {
    
    /**
     * Returns the index of the current active window, or null if outside all windows.
     */
    fun getCurrentWindowIndex(windows: List<TimeWindow>): Int? {
        val currentTime = timeProvider.currentTime()
        return windows.firstOrNull { isTimeInWindow(currentTime, it) }?.index
    }
    
    /**
     * Checks if a given time is within a window.
     */
    fun isTimeInWindow(time: LocalTime, window: TimeWindow): Boolean {
        val start = LocalTime.of(window.startHour, window.startMinute)
        val end = LocalTime.of(window.endHour, window.endMinute)
        return !time.isBefore(start) && time.isBefore(end)
    }
    
    /**
     * Checks if current time is within any window.
     */
    fun isInAnyWindow(windows: List<TimeWindow>): Boolean {
        return getCurrentWindowIndex(windows) != null
    }
}

