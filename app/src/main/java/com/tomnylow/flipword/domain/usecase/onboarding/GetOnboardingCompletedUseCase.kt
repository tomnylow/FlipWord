package com.tomnylow.flipword.domain.usecase.onboarding

import com.tomnylow.flipword.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetOnboardingCompletedUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.session.map { it.tutorialFinished }
}
