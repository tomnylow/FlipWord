package com.tomnylow.flipword.domain.usecase.settings

import com.tomnylow.flipword.domain.repository.SettingsRepository
import com.tomnylow.flipword.domain.repository.UserRepository
import com.tomnylow.flipword.domain.usecase.user.StartRepeatWorkerUseCase
import com.tomnylow.flipword.domain.usecase.user.StopRepeatWorkerUseCase
import javax.inject.Inject

class UpdateNotificationTimeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val startRepeatWorkerUseCase: StartRepeatWorkerUseCase
) {
    suspend operator fun invoke(hour: Int, minute: Int){
        settingsRepository.updateNotificationsTime(hour, minute)
        startRepeatWorkerUseCase()
    }
}