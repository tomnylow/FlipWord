package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.repository.AuthRepository
import com.tomnylow.flipword.domain.repository.SessionRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val result = authRepository.signOut()
        result.getOrNull()?.let {
            sessionRepository.clearUser()
        }
        return result
    }
}