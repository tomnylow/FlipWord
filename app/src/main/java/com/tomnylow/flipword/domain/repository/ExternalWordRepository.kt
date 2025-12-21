package com.tomnylow.flipword.domain.repository

interface ExternalWordRepository {
    suspend fun translate(word: String, from: String = "en", to: String = "ru"): Result<String>
    suspend fun getDefinition(word: String): Result<String>
    suspend fun getUsageExamples(word: String): Result<List<String>>
}
