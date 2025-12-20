package com.tomnylow.flipword

import com.tomnylow.flipword.domain.sm2.Rating
import com.tomnylow.flipword.domain.sm2.SM2Algorithm
import com.tomnylow.flipword.domain.sm2.SM2Params


import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SM2AlgorithmTest {
    private val today = LocalDate.now()

    // Тесты для calculateNextReview с низкими оценками (AGAIN)
    @Test
    fun `when rating is AGAIN should reset interval and repetition`() {
        val currentParams = SM2Params(
            easeFactor = 2.5,
            interval = 10,
            repetition = 5,
            nextReviewDate = today.plusDays(5)
        )

        val result = SM2Algorithm.calculateNextReview(currentParams, Rating.AGAIN, today)

        assertEquals(2.3, result.easeFactor, 0.01)
        assertEquals(1, result.interval)
        assertEquals(0, result.repetition)
        assertEquals(today.plusDays(1), result.nextReviewDate)
    }

    @Test
    fun `when rating is AGAIN with high ease factor should decrease it`() {
        val currentParams = SM2Params(
            easeFactor = 2.8,
            interval = 10,
            repetition = 5,
            nextReviewDate = today.plusDays(5)
        )

        val result = SM2Algorithm.calculateNextReview(currentParams, Rating.AGAIN, today)

        assertEquals(2.6, result.easeFactor, 0.01)
        assertEquals(1, result.interval)
        assertEquals(0, result.repetition)
    }
    // Тесты для NORMAL rating (первое повторение)
    @Test
    fun `when rating is NORMAL on first repetition should set interval to 1`() {
        val currentParams = SM2Params(
            easeFactor = 2.5,
            interval = 1,
            repetition = 0,
            nextReviewDate = today
        )

        val result = SM2Algorithm.calculateNextReview(currentParams, Rating.NORMAL, today)

        assertEquals(2.5, result.easeFactor, 0.01)
        assertEquals(1, result.interval)
        assertEquals(1, result.repetition)
        assertEquals(today.plusDays(1), result.nextReviewDate)
    }

    // Тесты для PERFECT rating
    @Test
    fun `when rating is PERFECT on first repetition should set interval to 1`() {
        val currentParams = SM2Params.INITIAL

        val result = SM2Algorithm.calculateNextReview(currentParams, Rating.PERFECT, today)

        assertEquals(2.6, result.easeFactor, 0.01) // 2.5 + (0.1 - (5-5)*(...)) = 2.6
        assertEquals(1, result.interval)
        assertEquals(1, result.repetition)
        assertEquals(today.plusDays(1), result.nextReviewDate)
    }

    @Test
    fun `when rating is PERFECT on second repetition should set interval to 6`() {
        val currentParams = SM2Params(
            easeFactor = 2.5,
            interval = 1,
            repetition = 1,
            nextReviewDate = today
        )

        val result = SM2Algorithm.calculateNextReview(currentParams, Rating.PERFECT, today)

        assertEquals(2.6, result.easeFactor, 0.01)
        assertEquals(6, result.interval)
        assertEquals(2, result.repetition)
        assertEquals(today.plusDays(6), result.nextReviewDate)
    }

    @Test
    fun `when rating is NORMAL on third repetition should calculate interval using ease factor`() {
        val currentParams = SM2Params(
            easeFactor = 2.5,
            interval = 6,
            repetition = 2,
            nextReviewDate = today
        )

        val result = SM2Algorithm.calculateNextReview(currentParams, Rating.NORMAL, today)

        val newEaseFactor = 2.5 // 2.5 + (0.1 - (5-4)*(0.08 + (5-4)*0.02))
        val expectedInterval = (6 * newEaseFactor).toInt()

        assertEquals(newEaseFactor, result.easeFactor, 0.01)
        assertEquals(expectedInterval, result.interval)
        assertEquals(3, result.repetition)
        assertEquals(today.plusDays(expectedInterval.toLong()), result.nextReviewDate)
    }



    // Тесты для isTimeForReview
    @Test
    fun `isTimeForReview should return true when next review date is today`() {
        val params = SM2Params(
            easeFactor = 2.5,
            interval = 1,
            repetition = 0,
            nextReviewDate = today
        )

        assertTrue(SM2Algorithm.isTimeForReview(params, today))
    }

    @Test
    fun `isTimeForReview should return true when next review date is in past`() {
        val params = SM2Params(
            easeFactor = 2.5,
            interval = 1,
            repetition = 0,
            nextReviewDate = today.minusDays(1)
        )

        assertTrue(SM2Algorithm.isTimeForReview(params, today))
    }

    @Test
    fun `isTimeForReview should return false when next review date is in future`() {
        val params = SM2Params(
            easeFactor = 2.5,
            interval = 1,
            repetition = 0,
            nextReviewDate = today.plusDays(1)
        )

        assertFalse(SM2Algorithm.isTimeForReview(params, today))
    }

    @Test
    fun `isTimeForReview with custom date should work correctly`() {
        val params = SM2Params(
            easeFactor = 2.5,
            interval = 1,
            repetition = 0,
            nextReviewDate = today.plusDays(5)
        )

        // Проверка через 3 дня
        assertFalse(SM2Algorithm.isTimeForReview(params, today.plusDays(3)))
        // Проверка через 5 дней
        assertTrue(SM2Algorithm.isTimeForReview(params, today.plusDays(5)))
        // Проверка через 7 дней
        assertTrue(SM2Algorithm.isTimeForReview(params, today.plusDays(7)))
    }



    // Тест для проверки целостности данных после вычислений
    @Test
    fun `calculateNextReview should maintain data consistency`() {
        val testCases = listOf(
            Triple(SM2Params.INITIAL, Rating.AGAIN, 1),
            Triple(SM2Params.INITIAL, Rating.NORMAL, 1),
            Triple(SM2Params.INITIAL, Rating.PERFECT, 1),
            Triple(
                SM2Params(
                    easeFactor = 2.5,
                    interval = 6,
                    repetition = 2,
                    nextReviewDate = today
                ),
                Rating.PERFECT,
                15
            )
        )

        for ((params, rating, expectedMinInterval) in testCases) {
            val result = SM2Algorithm.calculateNextReview(params, rating, today)

            // Проверка базовых инвариантов
            assertTrue(result.easeFactor >= 1.3, "Ease factor should be >= 1.3")
            assertTrue(result.interval >= expectedMinInterval, "Interval should be >= $expectedMinInterval")
            assertTrue(result.repetition >= 0, "Repetition should be >= 0")
            assertFalse(result.nextReviewDate.isBefore(today), "Next review date should not be in past")
        }
    }
}
