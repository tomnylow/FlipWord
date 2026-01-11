package com.tomnylow.flipword.domain.usecase.external

import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import javax.inject.Inject

class GetTranslationUseCase @Inject constructor(
    private val repository: ExternalWordRepository
) {
    suspend operator fun invoke(word: String, from: String = "en", to: String = "ru"): String? {
        if (word.isBlank()) return null
        return repository.translate(word, from, to)
    }
}
