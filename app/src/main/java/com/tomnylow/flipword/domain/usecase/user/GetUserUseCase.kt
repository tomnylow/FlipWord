package com.tomnylow.flipword.domain.usecase.user

import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.repository.AuthRepository
import com.tomnylow.flipword.domain.repository.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<User?> {
        return combine(
            authRepository.currentUser,
            sessionRepository.session
        ) { firebaseUser, localSession ->
            firebaseUser ?: localSession.user
        }
    }

}