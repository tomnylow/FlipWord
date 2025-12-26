package com.tomnylow.flipword.domain.usecase.external

import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import javax.inject.Inject

class GetTranslationUseCase @Inject constructor(
    private val repository: ExternalWordRepository
) {
    suspend operator fun invoke(word: String, from: String = "en", to: String = "ru"): Result<String> {
        if (word.isBlank()) return Result.failure(IllegalArgumentException("Word is empty"))
        return repository.translate(word, from, to)
    }
}
