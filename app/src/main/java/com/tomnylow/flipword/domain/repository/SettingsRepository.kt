package com.tomnylow.flipword.domain.repository

import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.domain.model.Settings
import com.tomnylow.flipword.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settingsFlow: Flow<Settings>

    suspend fun updateNativeLanguage(language: Language)

    suspend fun updateLearningLanguage(language: Language)

    suspend fun updateTheme(theme: AppTheme)

    suspend fun updateNotificationsEnabled(enabled: Boolean)
}
