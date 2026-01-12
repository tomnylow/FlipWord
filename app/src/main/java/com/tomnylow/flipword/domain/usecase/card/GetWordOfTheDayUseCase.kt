package com.tomnylow.flipword.domain.usecase.word

import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import javax.inject.Inject

class GetWordOfTheDayUseCase @Inject constructor(
    private val externalWordRepository: ExternalWordRepository
) {
    private val words = listOf(
        "magenta",
        "resilience",
        "ambivalent",
        "luminous",
        "ineffable",
        "gregarious",
        "surreptitious",
        "enigmatic",
        "pristine",
        "catalyst"
    )

    suspend operator fun invoke(): Pair<String, String?> {
        val word = words.random()
        val dictionaryData = externalWordRepository.getDictionaryData(word, "en")
        return Pair(word, dictionaryData?.definition)
    }
}
