package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User) {
        repository.createUser(user)
    }
}
