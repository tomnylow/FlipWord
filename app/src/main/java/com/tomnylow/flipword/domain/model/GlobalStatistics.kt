package com.tomnylow.flipword.domain.model

data class GlobalStatistics(
    val totalLearnedWords: Int,
    val accuracyPercentage: Float,
    val dayStreak: Int
)