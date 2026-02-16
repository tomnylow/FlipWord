package com.tomnylow.flipword.domain.usecase.word

import com.tomnylow.flipword.domain.model.Card
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
        val targetLanguageCode = settings.learningLanguage.code
        val nativeLanguageCode = settings.nativeLanguage.code

        var englishWord: String
        var targetWord: String?

        do {
            englishWord = externalWordRepository.getRandomWord().getOrElse {
                words.random()
            }
            targetWord = if (targetLanguageCode != "en") {
                externalWordRepository.translate(englishWord, "en", targetLanguageCode)
            } else {
                englishWord
            }
        } while (targetWord == null)

        val nativeTranslation = if (nativeLanguageCode != "en") {
            externalWordRepository.translate(englishWord, "en", nativeLanguageCode)
        } else {
            englishWord
        }

        val dictionaryData = externalWordRepository.getDictionaryData(englishWord, nativeLanguageCode)

        return Card(
            word = targetWord,
            definition = dictionaryData?.definition,
            translation = nativeTranslation,
            usageExample = dictionaryData?.example,
            deckId = -1
        )
    }
}
