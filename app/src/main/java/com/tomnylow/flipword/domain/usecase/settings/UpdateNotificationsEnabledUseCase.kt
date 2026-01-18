package com.tomnylow.flipword.domain.usecase.settings

import com.tomnylow.flipword.domain.repository.SettingsRepository

import javax.inject.Inject

class UpdateNotificationsEnabledUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.updateNotificationsEnabled(enabled)
    }
}