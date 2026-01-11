package com.tomnylow.flipword.domain.usecase.external

import com.tomnylow.flipword.domain.model.DictionaryData
import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import javax.inject.Inject

class GetDictionaryDataUseCase @Inject constructor(
    private val repository: ExternalWordRepository
) {
    suspend operator fun invoke(word: String, language: String = "en"): DictionaryData? {
        if (word.isBlank()) return null
        return repository.getDictionaryData(word, language)
    }
}
