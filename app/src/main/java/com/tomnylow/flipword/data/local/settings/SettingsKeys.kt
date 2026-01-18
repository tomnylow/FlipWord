package com.tomnylow.flipword.data.local.settings

import androidx.datastore.preferences.core.*

internal object SettingsKeys {
    val NATIVE_LANG_KEY = stringPreferencesKey("native_language")
    val LEARNING_LANG_KEY = stringPreferencesKey("learning_language")
    val THEME_KEY = stringPreferencesKey("theme")
    val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
    val NOTIFICATION_HOUR_KEY = intPreferencesKey("notification_hour")
    val NOTIFICATION_MINUTE_KEY = intPreferencesKey("notification_minute")
}