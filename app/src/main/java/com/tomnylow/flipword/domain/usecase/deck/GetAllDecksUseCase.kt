package com.tomnylow.flipword.domain.usecase.deck

import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.repository.DeckRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllDecksUseCase @Inject constructor(
    private val repository: DeckRepository
) {
    operator fun invoke(): Flow<List<Deck>> = repository.getAllDecks()
}
