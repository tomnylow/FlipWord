package com.tomnylow.flipword.domain.usecase.stats

import com.tomnylow.flipword.domain.model.GlobalStatistics
import com.tomnylow.flipword.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGlobalStatsUseCase @Inject constructor(
    private val repo: StatisticsRepository
) {
    operator fun invoke(): Flow<GlobalStatistics> = repo.getGlobalStats()
}