package com.tomnylow.flipword.domain.usecase.external

import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import javax.inject.Inject

class GetDefinitionUseCase @Inject constructor(
    private val repository: ExternalWordRepository
) {
    suspend operator fun invoke(word: String): Result<String> {
        if (word.isBlank()) return Result.failure(IllegalArgumentException("Word is empty"))
        return repository.getDefinition(word)
    }
}
