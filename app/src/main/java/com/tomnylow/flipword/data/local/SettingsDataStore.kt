package com.tomnylow.flipword.data.local

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

    val settingsFlow: Flow<Settings> = dataStore.data.map { preferences ->
        Settings(
            nativeLanguage = Language.fromCode(preferences[NATIVE_LANG_KEY] ?: "ru"),
            learningLanguage = Language.fromCode(preferences[LEARNING_LANG_KEY] ?: "en"),
            theme = AppTheme.fromName(preferences[THEME_KEY] ?: "SYSTEM"),
            notificationsEnabled = preferences[NOTIFICATIONS_KEY] ?: false,
            notificationHour = preferences[NOTIFICATION_HOUR_KEY] ?: 19,
            notificationMinute = preferences[NOTIFICATION_MINUTE_KEY] ?: 0
        )
    }

    suspend fun updateNativeLanguage(language: Language) {
        dataStore.edit { it[NATIVE_LANG_KEY] = language.code }
    }

    suspend fun updateLearningLanguage(language: Language) {
        dataStore.edit { it[LEARNING_LANG_KEY] = language.code }
    }

    suspend fun updateTheme(theme: AppTheme) {
        dataStore.edit { it[THEME_KEY] = theme.name }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_KEY] = enabled }
    }

    suspend fun updateNotificationTime(hour: Int, minute: Int) {
        dataStore.edit { prefs ->
            prefs[NOTIFICATION_HOUR_KEY] = hour
            prefs[NOTIFICATION_MINUTE_KEY] = minute
        }
    }

    companion object {
        private val NATIVE_LANG_KEY = stringPreferencesKey("native_language")
        private val LEARNING_LANG_KEY = stringPreferencesKey("learning_language")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
        private val NOTIFICATION_HOUR_KEY = intPreferencesKey("notification_hour")
        private val NOTIFICATION_MINUTE_KEY = intPreferencesKey("notification_minute")
    }
}