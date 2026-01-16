package com.tomnylow.flipword.data.repository

import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AuthRepositoryTest @Inject constructor(): AuthRepository {

    override val currentUser: StateFlow<User?>
        get() = TODO("Not yet implemented")

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun signOut(): Result<Unit> {
        TODO("Not yet implemented")
    }
}