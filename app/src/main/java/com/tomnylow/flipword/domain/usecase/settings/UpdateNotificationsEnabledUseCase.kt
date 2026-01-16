package com.tomnylow.flipword.domain.usecase.settings

import com.tomnylow.flipword.domain.repository.SettingsRepository
import com.tomnylow.flipword.domain.repository.UserRepository
import com.tomnylow.flipword.domain.usecase.user.StartRepeatWorkerUseCase
import com.tomnylow.flipword.domain.usecase.user.StopRepeatWorkerUseCase
import javax.inject.Inject

class UpdateNotificationsEnabledUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val startRepeatWorkerUseCase: StartRepeatWorkerUseCase,
    private val stopRepeatWorkerUseCase: StopRepeatWorkerUseCase
) {
    suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.updateNotificationsEnabled(enabled)
        if (enabled) {
            startRepeatWorkerUseCase()
        } else {
            stopRepeatWorkerUseCase()
        }
    }
}