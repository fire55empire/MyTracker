package com.mytracker.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

/**
 * Room entity representing a goal.
 * Only one active goal is allowed at a time.
 */
@Entity(tableName = "goals")
@TypeConverters(GoalConverters::class)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val startDate: LocalDate,
    val durationDays: Int,
    val timeWindows: List<TimeWindow>,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

/**
 * Represents a time window within a day.
 * All times are in local timezone.
 */
data class TimeWindow(
    val index: Int,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int
) {
    /**
     * Validates that this window is valid (doesn't cross midnight, end > start)
     */
    fun isValid(): Boolean {
        val startMinutes = startHour * 60 + startMinute
        val endMinutes = endHour * 60 + endMinute
        return startMinutes < endMinutes && 
               startHour in 0..23 && 
               endHour in 0..23 &&
               startMinute in 0..59 && 
               endMinute in 0..59
    }

    /**
     * Checks if this window overlaps with another window.
     */
    fun overlaps(other: TimeWindow): Boolean {
        val thisStart = startHour * 60 + startMinute
        val thisEnd = endHour * 60 + endMinute
        val otherStart = other.startHour * 60 + other.startMinute
        val otherEnd = other.endHour * 60 + other.endMinute
        
        return !(thisEnd <= otherStart || thisStart >= otherEnd)
    }

    fun formatTime(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }

    fun toDisplayString(): String {
        return "${formatTime(startHour, startMinute)} - ${formatTime(endHour, endMinute)}"
    }
}

/**
 * Type converters for Room to handle custom types.
 */
class GoalConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimeWindowList(value: List<TimeWindow>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTimeWindowList(value: String): List<TimeWindow> {
        val listType = object : TypeToken<List<TimeWindow>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }
}

