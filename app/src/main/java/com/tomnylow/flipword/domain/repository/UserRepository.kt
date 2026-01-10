package com.tomnylow.flipword.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getDailyStreak(): Flow<Int>
    suspend fun incrementDailyStreak()
    suspend fun resetDailyStreak()
}


