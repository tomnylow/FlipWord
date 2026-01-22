package com.tomnylow.flipword.domain.usecase.onboarding

import com.tomnylow.flipword.data.repository.OnboardingRepository
import javax.inject.Inject

class SetOnboardingCompletedUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke() {
        onboardingRepository.setOnboardingCompleted()
    }
}
