package com.tomnylow.flipword.domain.usecase.card

import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.repository.CardRepository
import com.tomnylow.flipword.domain.repository.DeckRepository
import com.tomnylow.flipword.domain.sm2.SM2Algorithm.isTimeForReview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllDueCardsUseCase @Inject constructor(
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Card>> {
        return deckRepository.getAllDecks().flatMapLatest { decks ->
            if (decks.isEmpty()) {
                flowOf(emptyList<Card>())
            } else {
                val flows = decks.map { deck ->
                    cardRepository.getCardsForDeck(deck.id)
                        .map { cards -> cards.filter { it.isTimeForReview() } }
                }
                combine(flows) { results -> results.flatMap { it } }
            }
        }
    }
}