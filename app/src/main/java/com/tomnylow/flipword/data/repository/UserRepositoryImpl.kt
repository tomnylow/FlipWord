package com.tomnylow.flipword.data.repository

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.tomnylow.flipword.domain.repository.UserRepository
import com.tomnylow.flipword.notifications.RepeatWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val workManager: WorkManager
) : UserRepository {
    override fun getDailyStreak(): Flow<Int> {
        return flowOf(7) // TODO
    }

    override suspend fun incrementDailyStreak() {
    }

    override suspend fun resetDailyStreak() {
    }

    override fun startRepeatWorker(targetHour: Int, targetMinute: Int) {

        val now = LocalDateTime.now()
        var target = now.withHour(targetHour).withMinute(targetMinute).withSecond(0).withNano(0)

        if (!target.isAfter(now)) {
            target = target.plusDays(1)
        }
        val zone = ZoneId.systemDefault()
        val nowInstant = now.atZone(zone).toInstant()
        val targetInstant = target.atZone(zone).toInstant()

        val delay = java.lang.Long.max(0, targetInstant.toEpochMilli() - nowInstant.toEpochMilli())

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true).build()

        val request = PeriodicWorkRequestBuilder<RepeatWorker>(
            24L, TimeUnit.HOURS
        )
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "RepeatWords",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request = request
        )

    }

    override fun stopRepeatWorker() {
        workManager.cancelUniqueWork("RepeatWords")
    }
}
