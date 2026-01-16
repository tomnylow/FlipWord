package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.repository.SettingsRepository
import com.tomnylow.flipword.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StartRepeatWorkerUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() {
        val settings = settingsRepository.settingsFlow.first()
        if (settings.notificationsEnabled)
            userRepository.startRepeatWorker(settings.notificationHour, settings.notificationMinute)
    }
}
