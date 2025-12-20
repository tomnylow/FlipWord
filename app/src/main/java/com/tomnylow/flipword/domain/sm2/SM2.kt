package com.tomnylow.flipword.domain.sm2

import java.time.LocalDate

data class SM2Params(
    val easeFactor: Double, // Фактор легкости (EF)
    val interval: Int,      // Интервал в днях до следующего повторения
    val repetition: Int,    // Количество успешных повторений подряд
    val nextReviewDate: LocalDate // Следующая дата повторения
) {
    companion object {
        val INITIAL = SM2Params(
            easeFactor = 2.5,
            interval = 1,
            repetition = 0,
            nextReviewDate = LocalDate.now()
        )
    }
}

enum class Rating(val value: Int) {
    AGAIN(2),
    NORMAL(4),
    PERFECT(5)
}


object SM2Algorithm {

    fun calculateNextReview(
        currentParams: SM2Params,
        rating: Rating,
        currentDate: LocalDate = LocalDate.now()
    ): SM2Params {
        return when {
            rating.value <= 2 -> {
                SM2Params(
                    easeFactor = maxOf(1.3, currentParams.easeFactor - 0.2),
                    interval = 1,
                    repetition = 0,
                    nextReviewDate = currentDate.plusDays(1)
                )
            }
            else -> {
                val newRepetition = currentParams.repetition + 1
                val newEaseFactor = calculateNewEaseFactor(currentParams.easeFactor, rating)

                val newInterval = when (newRepetition) {
                    1 -> 1
                    2 -> 6
                    else -> (currentParams.interval * newEaseFactor).toInt()
                }

                SM2Params(
                    easeFactor = newEaseFactor,
                    interval = newInterval,
                    repetition = newRepetition,
                    nextReviewDate = currentDate.plusDays(newInterval.toLong())
                )
            }
        }
    }


    private fun calculateNewEaseFactor(currentEaseFactor: Double, rating: Rating): Double {
        val q = rating.value
        // EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        val newEaseFactor = currentEaseFactor + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))

        return maxOf(1.3, newEaseFactor)
    }


    fun isTimeForReview(params: SM2Params, currentDate: LocalDate = LocalDate.now()): Boolean {
        return !params.nextReviewDate.isAfter(currentDate)
    }
}