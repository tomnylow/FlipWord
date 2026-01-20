package com.tomnylow.flipword.data.repository

import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AuthRepositoryTest @Inject constructor(): AuthRepository {

        private val _currentUser = MutableStateFlow<User?>(null)
        override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

        override suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
            if (email == "test@example.com" && password == "123456") {
                _currentUser.value = User(id = "1", email = email, displayName = "Test User")
            } else {
                throw IllegalArgumentException("Invalid email or password")
            }
        }

        override suspend fun signUp(name: String, email: String, password: String): Result<Unit> = runCatching {
            if (password.length < 6) {
                throw IllegalArgumentException("Password too short")
            }
            _currentUser.value = User(id = "mock-${System.currentTimeMillis()}", email = email, displayName = name)
        }

        override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {

        }

        override suspend fun signOut(): Result<Unit> = runCatching {
            _currentUser.value = null
        }
    }