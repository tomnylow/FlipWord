package com.tomnylow.flipword.data.repository

import com.tomnylow.flipword.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    // private val userDao: UserDao // Предполагается, что UserDao будет создан позже
) : UserRepository {
    override fun getDailyStreak(): Flow<Int> {
        // Заглушка, реальная реализация будет получать данные из DAO
        return flowOf(7)
    }

    override suspend fun incrementDailyStreak() {
        // Заглушка
    }

    override suspend fun resetDailyStreak() {
        // Заглушка
    }
}
