package com.tomnylow.flipword.domain.repository

import com.tomnylow.flipword.domain.model.Card
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    fun getCardsForDeck(deckId: Long): Flow<List<Card>>
    suspend fun getCardById(id: Long): Card?
    suspend fun insertCard(card: Card)
    suspend fun updateCard(card: Card)
    suspend fun deleteCard(card: Card)
}