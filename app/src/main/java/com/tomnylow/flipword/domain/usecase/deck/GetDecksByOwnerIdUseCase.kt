package com.tomnylow.flipword.domain.usecase.deck

import com.tomnylow.flipword.domain.repository.DeckRepository
import javax.inject.Inject

class GetDecksByOwnerIdUseCase @Inject constructor(
    private val repository: DeckRepository
) {
    operator fun invoke(ownerId: Long) = repository.getDecksByOwnerId(ownerId)
}
