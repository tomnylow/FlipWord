package com.tomnylow.flipword.domain.usecase.stats

import com.tomnylow.flipword.domain.repository.StatisticsRepository
import javax.inject.Inject

class LogStudyEventUseCase @Inject constructor(
    private val repository: StatisticsRepository
) {
    suspend operator fun invoke(cardId: Long, deckId: Long, rating: Int){
        repository.logStudyEvent(cardId, deckId, (rating >= 3))

    }
}