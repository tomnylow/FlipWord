package com.tomnylow.flipword.domain.usecase.card

import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.repository.CardRepository

import javax.inject.Inject

class InsertCardUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(card: Card) = repository.insertCard(card)
}
