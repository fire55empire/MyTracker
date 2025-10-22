package com.mytracker.app.domain.model

import com.mytracker.app.data.entity.GoalEntity
import com.mytracker.app.data.entity.TimeWindow
import java.time.LocalDate

/**
 * Domain model for a Goal.
 * This is a simplified, immutable representation for the UI layer.
 */
data class Goal(
    val id: Long,
    val title: String,
    val startDate: LocalDate,
    val durationDays: Int,
    val timeWindows: List<TimeWindow>,
    val windowsPerDay: Int = timeWindows.size
) {
    val totalRequiredPresses: Int
        get() = durationDays * windowsPerDay
        
    val endDate: LocalDate
        get() = startDate.plusDays(durationDays.toLong() - 1)
    
    fun isCompleted(totalPresses: Int): Boolean {
        return totalPresses >= totalRequiredPresses
    }
    
    fun calculateProgress(totalPresses: Int): Float {
        return if (totalRequiredPresses > 0) {
            (totalPresses.toFloat() / totalRequiredPresses.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }
    
    fun calculateProgressPercentage(totalPresses: Int): String {
        val percentage = calculateProgress(totalPresses) * 100
        return String.format("%.1f%%", percentage)
    }
}

/**
 * Extension to convert GoalEntity to domain Goal model.
 */
fun GoalEntity.toDomain(): Goal {
    return Goal(
        id = id,
        title = title,
        startDate = startDate,
        durationDays = durationDays,
        timeWindows = timeWindows
    )
}

