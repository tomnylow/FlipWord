package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<Unit> =
        authRepository.signUp(name, email, password)
}