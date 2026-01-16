package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        authRepository.signIn(email, password)
}