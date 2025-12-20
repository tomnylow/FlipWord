package com.tomnylow.flipword.data.repository

import com.tomnylow.flipword.data.local.CardDao
import com.tomnylow.flipword.data.local.model.toDomain
import com.tomnylow.flipword.data.local.model.toEntity
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao
) : CardRepository {
    override fun getCardsForDeck(deckId: Long): Flow<List<Card>> {
        return cardDao.getCardsForDeck(deckId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCardById(id: Long): Card? {
        return cardDao.getCardById(id)?.toDomain()
    }

    override suspend fun insertCard(card: Card) {
        cardDao.insertCard(card.toEntity())
    }

    override suspend fun updateCard(card: Card) {
        cardDao.updateCard(card.toEntity())
    }

    override suspend fun deleteCard(card: Card) {
        cardDao.deleteCard(card.toEntity())
    }
}
