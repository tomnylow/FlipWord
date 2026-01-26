package com.tomnylow.flipword.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupEntity(
    val decks: List<DeckEntity>,
    val cards: List<CardEntity>,
    val timestamp: Long = System.currentTimeMillis(),
    val version: Int = 1
)