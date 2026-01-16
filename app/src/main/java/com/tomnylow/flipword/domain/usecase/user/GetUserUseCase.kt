package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): StateFlow<User?> = authRepository.currentUser
}