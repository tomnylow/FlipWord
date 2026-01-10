package com.tomnylow.flipword.domain.usecase.word
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import java.util.Date
import javax.inject.Inject

class GetWordOfTheDayUseCase @Inject constructor(
    private val externalWordRepository: ExternalWordRepository
) {
    private val words = listOf(
        "kotlin", "jetpack", "compose", "android", "flow",
        "coroutine", "suspend", "viewmodel", "repository", "hilt"
    )
    suspend operator fun invoke(): Pair<String, Result<String>> {
        val word = words.random()
        return word to externalWordRepository.getDefinition(word, "en")
    }
}