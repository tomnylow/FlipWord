package com.tomnylow.flipword.domain.repository

import com.tomnylow.flipword.domain.model.DeckProgress
import com.tomnylow.flipword.domain.model.GlobalStatistics
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    fun getGlobalStats(): Flow<GlobalStatistics>
    fun getDecksProgress(): Flow<List<DeckProgress>>

    suspend fun logStudyEvent(cardId: Long, deckId: Long, isCorrect: Boolean)
}