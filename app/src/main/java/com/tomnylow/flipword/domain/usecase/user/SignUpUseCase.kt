package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.repository.AuthRepository
import com.tomnylow.flipword.domain.repository.SessionRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Unit> {
        val result = authRepository.signUp(name, email, password)
        if (result.isSuccess) {
            val user = authRepository.currentUser.value
            if (user != null) {
                sessionRepository.saveUser(user)
            }
        }
        return result
    }
}