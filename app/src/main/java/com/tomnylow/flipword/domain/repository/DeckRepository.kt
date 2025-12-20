package com.tomnylow.flipword.domain.repository

import com.tomnylow.flipword.domain.model.Deck
import kotlinx.coroutines.flow.Flow

interface DeckRepository {
    fun getAllDecks(): Flow<List<Deck>>

    suspend fun getDeckById(id: Long): Deck?
    suspend fun insertDeck(deck: Deck)
    suspend fun updateDeck(deck: Deck)
    suspend fun deleteDeck(deck: Deck)
}