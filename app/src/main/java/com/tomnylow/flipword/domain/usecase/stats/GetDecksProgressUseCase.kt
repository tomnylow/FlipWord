package com.tomnylow.flipword.domain.usecase.stats

import com.tomnylow.flipword.domain.model.DeckProgress
import com.tomnylow.flipword.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDecksProgressUseCase @Inject constructor(
    private val repo: StatisticsRepository
) {
    operator fun invoke(): Flow<List<DeckProgress>> = repo.getDecksProgress()
}