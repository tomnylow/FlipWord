package com.tomnylow.flipword.domain.repository

import com.tomnylow.flipword.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(name: String, email: String, password: String): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
}