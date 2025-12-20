package com.tomnylow.flipword.domain.repository

import com.tomnylow.flipword.domain.model.User

interface UserRepository {

        suspend fun getUser(userId: String): User?
        suspend fun getUserByEmail(email: String): User?
        suspend fun createUser(user: User): String
        suspend fun updateUser(user: User)
        suspend fun deleteUser(userId: String)

}