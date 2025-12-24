package com.tomnylow.flipword.data.repository

import com.tomnylow.flipword.data.remote.DictionaryApi
import com.tomnylow.flipword.data.remote.TranslationApi
import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class ExternalWordRepositoryImpl @Inject constructor(
    private val dictionaryApi: DictionaryApi,
    private val translationApi: TranslationApi
) : ExternalWordRepository {

    override suspend fun translate(word: String, from: String, to: String): Result<String> {
        return try {
            val response = translationApi.translate(text = word, from = from, to = to)
            val translatedText = parseGoogleTranslation(response)
            Result.success(translatedText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDefinition(word: String, language: String): Result<String> {
        return try {
            val dictionaryResponse = dictionaryApi.getDefinition(word)
            val engDefinition = dictionaryResponse.firstOrNull()
                ?.meanings?.firstOrNull()
                ?.definitions?.firstOrNull()
                ?.definition ?: return Result.failure(Exception("Definition not found"))

            if (language == "en") {
                Result.success(engDefinition)
            } else {
                val translationResponse = translationApi.translate(text = engDefinition, from = "en", to = language)
                Result.success(parseGoogleTranslation(translationResponse))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUsageExamples(word: String, language: String): Result<List<String>> {
        return try {
            val dictionaryResponse = dictionaryApi.getDefinition(word)
            val examples = dictionaryResponse.flatMap { dto ->
                dto.meanings?.flatMap { meaning ->
                    meaning.definitions?.mapNotNull { it.example } ?: emptyList()
                } ?: emptyList()
            }.distinct().take(3)

            if (language == "en" || examples.isEmpty()) {
                Result.success(examples)
            } else {

                val translatedExamples = examples.map { ex ->
                    val trans = translationApi.translate(text = ex, from = "en", to = language.split("-").last())
                    parseGoogleTranslation(trans)
                }
                Result.success(translatedExamples)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun parseGoogleTranslation(json: kotlinx.serialization.json.JsonElement): String {
        return try {
            json.jsonArray[0].jsonArray[0].jsonArray[0].jsonPrimitive.content
        } catch (_: Exception) {
            "Error parsing translation"
        }
    }
}
