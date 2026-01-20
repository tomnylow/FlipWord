package com.tomnylow.flipword.domain.repository

import com.tomnylow.flipword.domain.model.Session
import com.tomnylow.flipword.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    val session: StateFlow<Session>

    suspend fun saveUser(user: User)
    suspend fun clearUser()
    suspend fun setTutorialFinished(finished: Boolean)
}