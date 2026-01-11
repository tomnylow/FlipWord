package com.tomnylow.flipword.domain.repository

import com.tomnylow.flipword.ui.screens.profile.ProfileState
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settingsFlow: Flow<ProfileState>

    suspend fun updateNativeLanguage(language: String)

    suspend fun updateLearningLanguage(language: String)

    suspend fun updateTheme(theme: String)

    suspend fun updateNotificationsEnabled(enabled: Boolean)
}
