package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> =
        authRepository.signOut()
}