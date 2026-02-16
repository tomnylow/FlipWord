package com.tomnylow.flipword.data.local.model

import com.tomnylow.flipword.data.local.stats.StudyLogEntity
import kotlinx.serialization.Serializable

@Serializable
data class BackupEntity(
    val decks: List<DeckEntity>,
    val cards: List<CardEntity>,
    val stats: List<StudyLogEntity>,
    val timestamp: Long = System.currentTimeMillis(),
    val version: Int = 1
)