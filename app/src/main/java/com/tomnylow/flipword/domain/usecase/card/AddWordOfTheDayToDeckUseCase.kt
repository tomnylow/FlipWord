package com.tomnylow.flipword.domain.usecase.card

import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.repository.CardRepository
import com.tomnylow.flipword.domain.usecase.word.GetWordOfTheDayUseCase
import javax.inject.Inject

class AddWordOfTheDayToDeckUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(wordOfTheDayCard: Card, deckId: Long) {
        if (wordOfTheDayCard.translation != null) {
            cardRepository.insertCard(
                wordOfTheDayCard.copy(deckId = deckId)
            )
        }
    }
}