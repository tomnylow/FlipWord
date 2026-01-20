package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.repository.AuthRepository
import com.tomnylow.flipword.domain.repository.SessionRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<User> {
        val result = authRepository.signUp(name, email, password)
        result.getOrNull()?.let {
            sessionRepository.saveUser(it)
        }
        return result
    }
}