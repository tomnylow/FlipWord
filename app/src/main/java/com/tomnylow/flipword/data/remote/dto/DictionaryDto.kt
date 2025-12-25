package com.tomnylow.flipword.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DictionaryDto(
    val word: String? = null,
    val meanings: List<MeaningDto>? = null
)

@Serializable
data class MeaningDto(
    val definitions: List<DefinitionDto>? = null
)

@Serializable
data class DefinitionDto(
    val definition: String? = null,
    val example: String? = null
)
