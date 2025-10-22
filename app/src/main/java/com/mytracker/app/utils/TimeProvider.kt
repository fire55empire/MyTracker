package com.mytracker.app.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface to abstract system clock for easier testing.
 */
interface TimeProvider {
    fun currentDate(): LocalDate
    fun currentTime(): LocalTime
    fun currentDateTime(): LocalDateTime
    fun currentTimeMillis(): Long
}

/**
 * Production implementation using system clock.
 */
@Singleton
class SystemTimeProvider @Inject constructor() : TimeProvider {
    override fun currentDate(): LocalDate = LocalDate.now()
    override fun currentTime(): LocalTime = LocalTime.now()
    override fun currentDateTime(): LocalDateTime = LocalDateTime.now()
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}

