package com.tomnylow.flipword.domain.usecase.deck

import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.repository.DeckRepository
import javax.inject.Inject

class DeleteDeckUseCase @Inject constructor(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(deck: Deck) = repository.deleteDeck(deck)
}
