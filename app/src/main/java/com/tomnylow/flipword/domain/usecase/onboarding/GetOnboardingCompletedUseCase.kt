package com.tomnylow.flipword.domain.usecase.onboarding

import com.tomnylow.flipword.data.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnboardingCompletedUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    operator fun invoke(): Flow<Boolean> = onboardingRepository.hasCompletedOnboarding()
}
