package com.tomnylow.flipword.data.local.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.tomnylow.flipword.domain.model.AppTheme
import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.domain.model.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    val settingsFlow: Flow<Settings> = dataStore.data.map { prefs ->
        Settings(
            nativeLanguage = Language.fromCode(
                prefs[SettingsKeys.NATIVE_LANG_KEY] ?: Settings.SettingsDefaults.nativeLanguage.code
            ),
            learningLanguage = Language.fromCode(
                prefs[SettingsKeys.LEARNING_LANG_KEY]
                    ?: Settings.SettingsDefaults.learningLanguage.code
            ),
            theme = AppTheme.fromName(
                prefs[SettingsKeys.THEME_KEY] ?: Settings.SettingsDefaults.theme.name
            ),
            notificationsEnabled = prefs[SettingsKeys.NOTIFICATIONS_KEY]
                ?: Settings.SettingsDefaults.notificationsEnabled,
            notificationHour = prefs[SettingsKeys.NOTIFICATION_HOUR_KEY]
                ?: Settings.SettingsDefaults.notificationHour,
            notificationMinute = prefs[SettingsKeys.NOTIFICATION_MINUTE_KEY]
                ?: Settings.SettingsDefaults.notificationMinute
        )
    }

    suspend fun updateNativeLanguage(language: Language) {
        dataStore.edit { it[SettingsKeys.NATIVE_LANG_KEY] = language.code }
    }

    suspend fun updateLearningLanguage(language: Language) {
        dataStore.edit { it[SettingsKeys.LEARNING_LANG_KEY] = language.code }
    }

    suspend fun updateTheme(theme: AppTheme) {
        dataStore.edit { it[SettingsKeys.THEME_KEY] = theme.name }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.NOTIFICATIONS_KEY] = enabled }
    }

    suspend fun updateNotificationTime(hour: Int, minute: Int) {
        dataStore.edit {
            it[SettingsKeys.NOTIFICATION_HOUR_KEY] = hour
            it[SettingsKeys.NOTIFICATION_MINUTE_KEY] = minute
        }
    }
}