package com.tomnylow.flipword.domain.usecase.card

import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.repository.CardRepository
import javax.inject.Inject

class GetCardByIdUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(id: Long): Card? = repository.getCardById(id)
}
