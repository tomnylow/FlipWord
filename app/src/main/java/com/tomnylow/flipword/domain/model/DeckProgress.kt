package com.tomnylow.flipword.domain.model

data class DeckProgress(
    val deckId: Long,
    val totalCards: Int,
    val learnedCards: Int
) {
    val progressPercent: Float
        get() = if (totalCards > 0) (learnedCards.toFloat() / totalCards) else 0f
}