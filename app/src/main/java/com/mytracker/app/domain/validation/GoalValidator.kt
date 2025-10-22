package com.mytracker.app.domain.validation

import com.mytracker.app.data.entity.TimeWindow

/**
 * Validation result for goal creation.
 */
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

/**
 * Validates goal creation input.
 */
object GoalValidator {
    
    fun validateTitle(title: String): ValidationResult {
        return when {
            title.isBlank() -> ValidationResult.Error("Title cannot be empty")
            title.length > 100 -> ValidationResult.Error("Title is too long (max 100 characters)")
            else -> ValidationResult.Success
        }
    }
    
    fun validateDurationDays(days: Int): ValidationResult {
        return when {
            days <= 0 -> ValidationResult.Error("Duration must be at least 1 day")
            days > 365 -> ValidationResult.Error("Duration cannot exceed 365 days")
            else -> ValidationResult.Success
        }
    }
    
    fun validateTimeWindows(windows: List<TimeWindow>): ValidationResult {
        if (windows.isEmpty()) {
            return ValidationResult.Error("At least one time window is required")
        }
        
        // Validate each window individually
        windows.forEach { window ->
            if (!window.isValid()) {
                return ValidationResult.Error(
                    "Invalid time window: ${window.toDisplayString()}. " +
                    "End time must be after start time and within the same day."
                )
            }
        }
        
        // Check for overlapping windows
        for (i in windows.indices) {
            for (j in i + 1 until windows.size) {
                if (windows[i].overlaps(windows[j])) {
                    return ValidationResult.Error(
                        "Time windows cannot overlap: ${windows[i].toDisplayString()} " +
                        "and ${windows[j].toDisplayString()}"
                    )
                }
            }
        }
        
        return ValidationResult.Success
    }
    
    fun validateGoal(title: String, durationDays: Int, windows: List<TimeWindow>): ValidationResult {
        validateTitle(title).let { if (it is ValidationResult.Error) return it }
        validateDurationDays(durationDays).let { if (it is ValidationResult.Error) return it }
        validateTimeWindows(windows).let { if (it is ValidationResult.Error) return it }
        return ValidationResult.Success
    }
}

