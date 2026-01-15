package com.tomnylow.flipword.domain.repository

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import com.tomnylow.flipword.notifications.RepeatWorker
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

interface UserRepository {
    fun getDailyStreak(): Flow<Int>
    suspend fun incrementDailyStreak()
    suspend fun resetDailyStreak()
    fun startRepeatWorker(targetHour: Int, targetMinute: Int)
}


