package com.tomnylow.flipword.domain.usecase.external

import com.tomnylow.flipword.domain.model.DictionaryData
import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import javax.inject.Inject

class GetDictionaryDataUseCase @Inject constructor(
    private val repository: ExternalWordRepository
) {
    suspend operator fun invoke(word: String, queryLanguage: Language): DictionaryData? {
        if (word.isBlank()) return null
        val englishWord = if (queryLanguage.code == "en") word else repository.translate(word, queryLanguage.code, "en")
        if (englishWord == null) return null
        return repository.getDictionaryData(englishWord, queryLanguage.code)
    }
}
