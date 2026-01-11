package com.tomnylow.flipword.domain.usecase.word

import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import javax.inject.Inject

class GetWordOfTheDayUseCase @Inject constructor(
    private val externalWordRepository: ExternalWordRepository
) {
    private val words = listOf(
        "kotlin", "jetpack", "compose", "android", "flow",
        "coroutine", "suspend", "viewmodel", "repository", "hilt"
    )

    suspend operator fun invoke(): Pair<String, String?> {
        val word = words.random()
        val dictionaryData = externalWordRepository.getDictionaryData(word, "en")
        return Pair(word, dictionaryData?.definition)
    }
}
