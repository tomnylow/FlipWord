package com.tomnylow.flipword.data.repository

import com.tomnylow.flipword.data.local.stats.StatisticsDao
import com.tomnylow.flipword.data.local.stats.StudyLogEntity
import com.tomnylow.flipword.domain.model.DeckProgress
import com.tomnylow.flipword.domain.model.GlobalStatistics
import com.tomnylow.flipword.domain.repository.StatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val dao: StatisticsDao
) : StatisticsRepository {

    override fun getGlobalStats(): Flow<GlobalStatistics> {
        return combine(
            dao.getStudyDates(),
            dao.getTotalLearnedWords(),
            dao.getTotalAnswersCount(),
            dao.getCorrectAnswersCount()
        ) { dates, learnedCount, totalAnswers, correctAnswers ->


            val streak = calculateStreak(dates)
            val accuracy =
                if (totalAnswers > 0) (correctAnswers.toFloat() / totalAnswers) * 100 else 0f

            GlobalStatistics(
                totalLearnedWords = learnedCount,
                accuracyPercentage = accuracy,
                dayStreak = streak
            )
        }
            .flowOn(Dispatchers.IO)
    }

    override fun getDecksProgress(): Flow<List<DeckProgress>> {
        return dao.getDecksProgressRaw().map { list ->
            list.map {
                DeckProgress(
                    deckId = it.deckId,
                    totalCards = it.totalCards,
                    learnedCards = it.learnedCards
                )
            }
        }
    }

    override suspend fun logStudyEvent(cardId: Long, deckId: Long, isCorrect: Boolean) {
        dao.insertLog(
            StudyLogEntity(
                cardId = cardId,
                deckId = deckId,
                isCorrect = isCorrect,
                date = LocalDate.now()
            )
        )
    }

    private fun calculateStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val lastStudyDate = dates.first()

        if (lastStudyDate != today && lastStudyDate != yesterday) {
            return 0
        }

        var streak = 0
        var expectedDate = lastStudyDate

        for (date in dates) {
            if (date == expectedDate) {
                streak++
                expectedDate = expectedDate.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }
}