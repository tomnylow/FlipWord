package com.tomnylow.flipword.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface OnboardingRepository {
    fun hasCompletedOnboarding(): Flow<Boolean>
    suspend fun setOnboardingCompleted()
}

class MockOnboardingRepository @Inject constructor() : OnboardingRepository {
    override fun hasCompletedOnboarding(): Flow<Boolean> {
        return flowOf(false)
    }

    override suspend fun setOnboardingCompleted() {
    }
}
