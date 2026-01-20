package com.tomnylow.flipword.domain.repository

import com.tomnylow.flipword.domain.model.Session
import com.tomnylow.flipword.domain.model.User
import kotlinx.coroutines.flow.Flow


interface SessionRepository {
    val session: Flow<Session>

    suspend fun saveUser(user: User)
    suspend fun clearUser()
    suspend fun setTutorialFinished(finished: Boolean)
}