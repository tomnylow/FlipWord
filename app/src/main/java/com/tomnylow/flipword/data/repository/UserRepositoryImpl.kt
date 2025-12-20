package com.tomnylow.flipword.data.repository

import com.tomnylow.flipword.data.local.UserDao
import com.tomnylow.flipword.data.local.model.toDomain
import com.tomnylow.flipword.data.local.model.toEntity
import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun getUser(userId: Long): User? {
        return userDao.getUserById(userId)?.toDomain()
    }

    override suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)?.toDomain()
    }

    override suspend fun createUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun deleteUser(userId: Long) {
        userDao.deleteUser(userId)
    }
}
