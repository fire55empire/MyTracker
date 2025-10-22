package com.mytracker.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mytracker.app.data.dao.GoalDao
import com.mytracker.app.data.dao.PressRecordDao
import com.mytracker.app.data.entity.GoalConverters
import com.mytracker.app.data.entity.GoalEntity
import com.mytracker.app.data.entity.PressRecordEntity

/**
 * Room database for MyTracker app.
 */
@Database(
    entities = [GoalEntity::class, PressRecordEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(GoalConverters::class)
abstract class MyTrackerDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun pressRecordDao(): PressRecordDao
}

