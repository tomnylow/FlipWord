package com.tomnylow.flipword.domain.usecase.settings

import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateNativeLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(language: Language) {
        repository.updateNativeLanguage(language)
    }
}