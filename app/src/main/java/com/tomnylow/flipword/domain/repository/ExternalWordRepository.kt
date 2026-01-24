package com.tomnylow.flipword.domain.repository

import com.tomnylow.flipword.domain.model.DictionaryData

interface ExternalWordRepository {
    suspend fun translate(word: String, from: String = "en", to: String = "ru"): String?
    suspend fun getDictionaryData(word: String, language: String = "en"): DictionaryData?
}
