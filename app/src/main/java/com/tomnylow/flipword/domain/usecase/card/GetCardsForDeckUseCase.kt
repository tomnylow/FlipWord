package com.tomnylow.flipword.domain.usecase.card

import com.tomnylow.flipword.domain.repository.CardRepository
import javax.inject.Inject

class GetCardsForDeckUseCase @Inject constructor(
    private val repository: CardRepository
) {
    operator fun invoke(deckId: Long) = repository.getCardsForDeck(deckId)
}
