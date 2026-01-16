package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.repository.SettingsRepository
import com.tomnylow.flipword.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StopRepeatWorkerUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.stopRepeatWorker()
    }
}