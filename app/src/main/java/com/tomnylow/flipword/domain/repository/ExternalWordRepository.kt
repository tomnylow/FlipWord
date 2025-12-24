package com.tomnylow.flipword.domain.repository

interface ExternalWordRepository {
    suspend fun translate(word: String, from: String = "en", to: String = "ru"): Result<String>
    suspend fun getDefinition(word: String, language: String = "en"): Result<String>
    suspend fun getUsageExamples(word: String, language: String = "en"): Result<List<String>>
}
