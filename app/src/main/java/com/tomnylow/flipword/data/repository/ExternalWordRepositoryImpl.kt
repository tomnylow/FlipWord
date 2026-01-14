package com.tomnylow.flipword.data.repository

import com.tomnylow.flipword.data.remote.DictionaryApi
import com.tomnylow.flipword.data.remote.TranslationApi
import com.tomnylow.flipword.domain.model.DictionaryData
import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class ExternalWordRepositoryImpl @Inject constructor(
    private val dictionaryApi: DictionaryApi,
    private val translationApi: TranslationApi
) : ExternalWordRepository {

    override suspend fun translate(word: String, from: String, to: String): String? {
        return try {
            val response = translationApi.translate(text = word, from = from, to = to)
            parseGoogleTranslation(response)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getDictionaryData(word: String, answerLanguage: String): DictionaryData? {
        return try {
            val dictionaryResponse = dictionaryApi.getDefinition(word.trim())
            val firstMeaning = dictionaryResponse.firstOrNull()?.meanings?.firstOrNull()
            val definition = firstMeaning?.definitions?.firstOrNull()?.definition
            val example = firstMeaning?.definitions?.firstOrNull()?.example

            if (answerLanguage == "en") {
                DictionaryData(definition, example)
            } else {
                coroutineScope {
                    val definitionDeferred = async {
                        definition?.let { def ->
                            translationApi.translate(text = def, from = "en", to = answerLanguage)
                                .let { parseGoogleTranslation(it) }
                        }
                    }
                    val exampleDeferred = async {
                        example?.let { ex ->
                            translationApi.translate(text = ex, from = "en", to = answerLanguage)
                                .let { parseGoogleTranslation(it) }
                        }
                    }
                    DictionaryData(definitionDeferred.await(), exampleDeferred.await())
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseGoogleTranslation(json: JsonElement): String? {
        return try {
            json.jsonArray[0].jsonArray[0].jsonArray[0].jsonPrimitive.content
        } catch (e: Exception) {
            null
        }
    }
}
