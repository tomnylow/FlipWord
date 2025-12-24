package com.tomnylow.flipword.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DictionaryDto(
    val word: String? = null,
    val phonetic: String? = null,
    val meanings: List<MeaningDto>? = null
)

@Serializable
data class MeaningDto(
    val partOfSpeech: String? = null,
    val definitions: List<DefinitionDto>? = null
)

@Serializable
data class DefinitionDto(
    val definition: String? = null,
    val example: String? = null,
    val synonyms: List<String>? = null,
    val antonyms: List<String>? = null
)
