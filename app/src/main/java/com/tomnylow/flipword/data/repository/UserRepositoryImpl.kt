package com.tomnylow.flipword.data.repository

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.tomnylow.flipword.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
) : UserRepository {
    override fun getDailyStreak(): Flow<Int> {
        return flowOf(7) // TODO
    }

    override suspend fun incrementDailyStreak() {
    }

    override suspend fun resetDailyStreak() {
    }
}

