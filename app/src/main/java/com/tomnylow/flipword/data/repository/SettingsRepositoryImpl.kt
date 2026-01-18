package com.tomnylow.flipword.data.repository

import com.tomnylow.flipword.data.local.settings.SettingsDataStore
import com.tomnylow.flipword.domain.model.AppTheme
import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.domain.model.Settings
import com.tomnylow.flipword.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {
    override val settingsFlow: Flow<Settings> = settingsDataStore.settingsFlow

    override suspend fun updateNativeLanguage(language: Language) =
        settingsDataStore.updateNativeLanguage(language)


    override suspend fun updateLearningLanguage(language: Language) =
        settingsDataStore.updateLearningLanguage(language)


    override suspend fun updateTheme(theme: AppTheme) =
        settingsDataStore.updateTheme(theme)


    override suspend fun updateNotificationsEnabled(enabled: Boolean) =
        settingsDataStore.updateNotificationsEnabled(enabled)

    override suspend fun updateNotificationsTime(hour: Int, minute: Int) {
        settingsDataStore.updateNotificationTime(hour, minute)
    }

}