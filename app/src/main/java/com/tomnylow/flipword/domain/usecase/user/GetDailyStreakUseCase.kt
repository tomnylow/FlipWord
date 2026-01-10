package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyStreakUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Int> {
        return userRepository.getDailyStreak()
    }
}
