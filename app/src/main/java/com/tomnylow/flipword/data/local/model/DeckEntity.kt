package com.tomnylow.flipword.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tomnylow.flipword.domain.model.Deck
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val updatedAt: Long = System.currentTimeMillis(),

)


