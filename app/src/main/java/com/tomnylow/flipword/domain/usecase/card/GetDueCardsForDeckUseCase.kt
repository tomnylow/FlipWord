package com.tomnylow.flipword.domain.usecase.card

import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.repository.CardRepository
import com.tomnylow.flipword.domain.sm2.SM2Algorithm.isTimeForReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class GetDueCardsForDeckUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {
    operator fun invoke(deckId: Long): Flow<List<Card>> {
        return cardRepository.getCardsForDeck(deckId).map { cards ->
            cards.filter { it.isTimeForReview() }
        }
    }
}
