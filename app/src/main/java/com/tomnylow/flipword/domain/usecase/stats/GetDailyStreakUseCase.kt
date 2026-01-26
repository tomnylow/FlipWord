package com.tomnylow.flipword.domain.usecase.stats

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetDailyStreakUseCase @Inject constructor(
    //private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Int> {
        return flowOf(7)
    }
}