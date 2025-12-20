package com.tomnylow.flipword.domain.usecase.deck

import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.repository.DeckRepository

import javax.inject.Inject

class GetDeckByIdUseCase @Inject constructor(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(id: Long): Deck? = repository.getDeckById(id)
}
