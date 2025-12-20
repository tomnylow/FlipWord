package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Long) {
        repository.deleteUser(userId)
    }
}
