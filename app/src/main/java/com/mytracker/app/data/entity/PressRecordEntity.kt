package com.mytracker.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate

/**
 * Room entity representing a single button press.
 * Each press is associated with a goal, a specific window, and a date.
 */
@Entity(
    tableName = "press_records",
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["goalId", "windowIndex", "date"], unique = true)]
)
@TypeConverters(GoalConverters::class)
data class PressRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val windowIndex: Int,
    val date: LocalDate,
    val timestamp: Long = System.currentTimeMillis()
)

