package com.tomnylow.flipword.data.repository

import com.tomnylow.flipword.data.local.DeckDao
import com.tomnylow.flipword.data.local.model.toDomain
import com.tomnylow.flipword.data.local.model.toEntity
import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.repository.DeckRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeckRepositoryImpl @Inject constructor(
    private val deckDao: DeckDao
) : DeckRepository {

    override fun getAllDecks(): Flow<List<Deck>> {
        return deckDao.getAllDecks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDeckById(id: Long): Deck? {
        return deckDao.getDeckById(id)?.toDomain()
    }

    override suspend fun insertDeck(deck: Deck) {
        deckDao.insertDeck(deck.toEntity())
    }

    override suspend fun updateDeck(deck: Deck) {
        deckDao.updateDeck(deck.toEntity())
    }

    override suspend fun deleteDeck(deck: Deck) {
        deckDao.deleteDeck(deck.toEntity())
    }
}
