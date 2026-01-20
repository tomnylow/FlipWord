package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.repository.AuthRepository
import com.tomnylow.flipword.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        val result = authRepository.signIn(email, password)
        result.getOrNull()?.let {
            sessionRepository.saveUser(it)
        }
        return result
    }
}