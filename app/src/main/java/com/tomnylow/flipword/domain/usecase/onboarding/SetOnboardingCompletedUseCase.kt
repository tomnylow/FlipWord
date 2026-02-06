package com.tomnylow.flipword.domain.usecase.onboarding

import com.tomnylow.flipword.domain.repository.SessionRepository
import javax.inject.Inject

class SetOnboardingCompletedUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(completed: Boolean) {
        repository.setTutorialFinished(completed)
    }
}
