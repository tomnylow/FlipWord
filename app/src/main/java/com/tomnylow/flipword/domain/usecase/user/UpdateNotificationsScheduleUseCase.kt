package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.notifications.NotificationScheduler
import javax.inject.Inject

class UpdateNotificationScheduleUseCase @Inject constructor(
    private val scheduler: NotificationScheduler
) {
    operator fun invoke(enabled: Boolean, hour: Int, minute: Int) {
        if (enabled) {
            scheduler.schedule(hour, minute)
        } else {
            scheduler.cancel()
        }
    }
}