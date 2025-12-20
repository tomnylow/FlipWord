package com.tomnylow.flipword.domain.model

import com.tomnylow.flipword.domain.sm2.SM2Params


data class Card(
    val id: Long = 0,
    val word: String,
    val translation: String? = null,
    val definition: String? = null,
    val usageExample: String? = null,
    val deckId: Long,
    val sm2Params: SM2Params = SM2Params.INITIAL
)
