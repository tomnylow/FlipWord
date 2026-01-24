package com.tomnylow.flipword.domain.usecase.settings

import com.tomnylow.flipword.domain.model.AppTheme
import com.tomnylow.flipword.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateAppThemeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(theme: AppTheme) {
        repository.updateTheme(theme)
    }
}