package com.tomnylow.flipword.domain.usecase.notifications

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.tomnylow.flipword.data.notifications.RepeatWorker
import com.tomnylow.flipword.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StartRepeatWorkerUseCase @Inject constructor(
    private val workManager: WorkManager,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() {
        val settings = settingsRepository.settingsFlow.first()
        if (settings.notificationsEnabled) {
            val now = LocalDateTime.now()
            var target =
                now.withHour(settings.notificationHour).withMinute(settings.notificationMinute)
                    .withSecond(0).withNano(0)

            if (!target.isAfter(now)) {
                target = target.plusDays(1)
            }
            val zone = ZoneId.systemDefault()
            val nowInstant = now.atZone(zone).toInstant()
            val targetInstant = target.atZone(zone).toInstant()

            val delay =
                java.lang.Long.max(0, targetInstant.toEpochMilli() - nowInstant.toEpochMilli())

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
    }
}
