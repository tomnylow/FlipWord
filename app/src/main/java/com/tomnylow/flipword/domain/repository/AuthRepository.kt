package com.tomnylow.flipword.domain.repository

import com.tomnylow.flipword.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<User?>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(name: String, email: String, password: String): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
}