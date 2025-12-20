package com.tomnylow.flipword.domain.usecase.card

import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.repository.CardRepository
import javax.inject.Inject

class UpdateCardUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(card: Card) = repository.updateCard(card)
}
