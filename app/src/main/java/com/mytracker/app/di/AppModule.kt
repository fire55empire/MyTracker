package com.mytracker.app.di

import android.content.Context
import androidx.room.Room
import com.mytracker.app.data.database.MyTrackerDatabase
import com.mytracker.app.utils.SystemTimeProvider
import com.mytracker.app.utils.TimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing app-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyTrackerDatabase {
        return Room.databaseBuilder(
            context,
            MyTrackerDatabase::class.java,
            "mytracker.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    @Singleton
    fun provideGoalDao(database: MyTrackerDatabase) = database.goalDao()
    
    @Provides
    @Singleton
    fun providePressRecordDao(database: MyTrackerDatabase) = database.pressRecordDao()
    
    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider = SystemTimeProvider()
}

