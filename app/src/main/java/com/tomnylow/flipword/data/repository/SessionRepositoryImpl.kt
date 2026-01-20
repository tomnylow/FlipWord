package com.tomnylow.flipword.data.repository

import com.tomnylow.flipword.data.local.session.SessionDataStore
import com.tomnylow.flipword.domain.model.Session
import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.repository.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : SessionRepository {

    override val session: StateFlow<Session> = sessionDataStore.sessionFlow
        .stateIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Session()
        )

    override suspend fun saveUser(user: User) =
        sessionDataStore.saveUser(user)

    override suspend fun clearUser() =
        sessionDataStore.clearUser()

    override suspend fun setTutorialFinished(finished: Boolean) =
        sessionDataStore.setTutorialFinished(finished)
}