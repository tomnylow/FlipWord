package com.tomnylow.flipword.domain.usecase.settings

import com.tomnylow.flipword.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateNotificationTimeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(hour: Int, minute: Int){
        settingsRepository.updateNotificationsTime(hour, minute)
    }
}