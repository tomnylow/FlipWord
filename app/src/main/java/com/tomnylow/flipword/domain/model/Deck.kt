package com.tomnylow.flipword.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Deck(
    val id: Long = 0,
    val name: String,
    val ownerId: Long
)
