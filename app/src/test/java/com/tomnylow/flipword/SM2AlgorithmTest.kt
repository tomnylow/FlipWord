package com.tomnylow.flipword.domain.sm2

import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.sm2.SM2Algorithm.isTimeForReview
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SM2AlgorithmTest {

    private val baseDate = LocalDate.of(2024, 1, 1)

    @Test
    fun `calculateNextReview AGAIN rating resets parameters`() {
        val initial = SM2Params.INITIAL.copy(nextReviewDate = baseDate)
        val result = SM2Algorithm.calculateNextReview(initial, Rating.AGAIN, baseDate)

        assertEquals(2.3, result.easeFactor, 0.001)
        assertEquals(0, result.interval)
        assertEquals(0, result.repetition)
        assertEquals(baseDate, result.nextReviewDate)
    }

    @Test
    fun `calculateNextReview NORMAL rating first repetition`() {
        val initial = SM2Params.INITIAL.copy(nextReviewDate = baseDate)
        val result = SM2Algorithm.calculateNextReview(initial, Rating.NORMAL, baseDate)

        assertEquals(2.36, result.easeFactor, 0.001)
        assertEquals(1, result.interval)
        assertEquals(1, result.repetition)
        assertEquals(baseDate.plusDays(1), result.nextReviewDate)
    }

    @Test
    fun `calculateNextReview PERFECT rating first repetition`() {
        val initial = SM2Params.INITIAL.copy(nextReviewDate = baseDate)
        val result = SM2Algorithm.calculateNextReview(initial, Rating.PERFECT, baseDate)

        assertEquals(2.6, result.easeFactor, 0.001)
        assertEquals(1, result.interval)
        assertEquals(1, result.repetition)
        assertEquals(baseDate.plusDays(1), result.nextReviewDate)
    }

    @Test
    fun `calculateNextReview NORMAL rating second repetition`() {
        val current = SM2Params(
            easeFactor = 2.36,
            interval = 1,
            repetition = 1,
            nextReviewDate = baseDate
        )
        val result = SM2Algorithm.calculateNextReview(current, Rating.NORMAL, baseDate)

        assertEquals(2.22, result.easeFactor, 0.001)
        assertEquals(6, result.interval)
        assertEquals(2, result.repetition)
        assertEquals(baseDate.plusDays(6), result.nextReviewDate)
    }

    @Test
    fun `calculateNextReview NORMAL rating third repetition`() {
        val current = SM2Params(
            easeFactor = 2.22,
            interval = 6,
            repetition = 2,
            nextReviewDate = baseDate
        )
        val result = SM2Algorithm.calculateNextReview(current, Rating.NORMAL, baseDate)

        assertEquals(2.08, result.easeFactor, 0.001)
        assertEquals(12, result.interval)
        assertEquals(3, result.repetition)
        assertEquals(baseDate.plusDays(12), result.nextReviewDate)
    }

    @Test
    fun `calculateNextReview PERFECT rating third repetition`() {
        val current = SM2Params(
            easeFactor = 2.22,
            interval = 6,
            repetition = 2,
            nextReviewDate = baseDate
        )
        val result = SM2Algorithm.calculateNextReview(current, Rating.PERFECT, baseDate)

        assertEquals(2.32, result.easeFactor, 0.001)
        assertEquals(13, result.interval)
        assertEquals(3, result.repetition)
        assertEquals(baseDate.plusDays(13), result.nextReviewDate)
    }

    @Test
    fun `calculateNextReview ease factor clamped to minimum 1_3`() {
        val current = SM2Params(
            easeFactor = 1.4,
            interval = 6,
            repetition = 2,
            nextReviewDate = baseDate
        )
        val result = SM2Algorithm.calculateNextReview(current, Rating.NORMAL, baseDate)

        assertEquals(1.3, result.easeFactor, 0.001)
    }

    @Test
    fun `isTimeForReview returns true when nextReviewDate is today`() {
        val params = SM2Params.INITIAL.copy(nextReviewDate = baseDate)
        val card = Card(
            word = "",
            deckId = 1, sm2Params = params
        )
        assertTrue(card.isTimeForReview(baseDate))
    }

    @Test
    fun `isTimeForReview returns true when nextReviewDate is in past`() {
        val params = SM2Params.INITIAL.copy(nextReviewDate = baseDate.minusDays(1))
        val card = Card(
            word = "",
            deckId = 1,
            sm2Params = params,
        )
        assertTrue(card.isTimeForReview(baseDate))
    }

    @Test
    fun `isTimeForReview returns false when nextReviewDate is in future`() {
        val params = SM2Params.INITIAL.copy(nextReviewDate = baseDate.plusDays(1))
        val card = Card(
            word = "",
            deckId = 1,
            sm2Params = params
        )
        assertFalse(card.isTimeForReview(baseDate))
    }
}