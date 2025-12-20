package com.tomnylow.flipword.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tomnylow.flipword.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String?,
    val name: String?
)

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        email = email,
        name = name
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        email = email,
        name = name
    )
}
