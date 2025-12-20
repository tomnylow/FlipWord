package com.tomnylow.flipword.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tomnylow.flipword.domain.model.Deck

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val ownerId: Long
)

fun DeckEntity.toDomain(): Deck {
    return Deck(
        id = id,
        name = name,
        ownerId = ownerId
    )
}

fun Deck.toEntity(): DeckEntity {
    return DeckEntity(
        id = id,
        name = name,
        ownerId = ownerId
    )
}
