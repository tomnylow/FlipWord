package com.tomnylow.flipword.domain.usecase.card

import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.repository.CardRepository
import com.tomnylow.flipword.domain.usecase.word.GetWordOfTheDayUseCase
import javax.inject.Inject

class AddWordOfTheDayToDeckUseCase @Inject constructor(
    private val cardRepository: CardRepository,
    private val getWordOfTheDayUseCase: GetWordOfTheDayUseCase
) {
    suspend operator fun invoke(deckId: Long) {
        val (word, definition) = getWordOfTheDayUseCase()
        if (definition != null) {
            cardRepository.insertCard(
                Card(
                    deckId = deckId,
                    word = word,
                    definition = definition
                )
            )
        }
    }
}