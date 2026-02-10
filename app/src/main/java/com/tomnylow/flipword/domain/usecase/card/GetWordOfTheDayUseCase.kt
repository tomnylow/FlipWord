package com.tomnylow.flipword.domain.usecase.word

import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import com.tomnylow.flipword.domain.usecase.settings.GetSettingsUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetWordOfTheDayUseCase @Inject constructor(
    private val externalWordRepository: ExternalWordRepository,
    private val getSettingsUseCase: GetSettingsUseCase
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

    suspend operator fun invoke(): Card {
        val settings = getSettingsUseCase().first()

        val wordResult = externalWordRepository.getRandomWord()
        val word = wordResult.getOrElse {
            words.random()
        }

        val dictionaryData = externalWordRepository.getDictionaryData(word, settings.nativeLanguage.code)

        val translatedWord = if (settings.nativeLanguage.code != "en") {
            externalWordRepository.translate(word, "en", settings.nativeLanguage.code)
        } else {
            word
        }

        return Card(
            word = word,
            definition = dictionaryData?.definition,
            translation = translatedWord,
            usageExample = dictionaryData?.example,
            deckId = -1
        )
    }
}
