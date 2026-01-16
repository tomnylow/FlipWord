package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.repository.AuthRepository
import javax.inject.Inject

class SendPasswordResetUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Unit> =
        authRepository.sendPasswordResetEmail(email)
}