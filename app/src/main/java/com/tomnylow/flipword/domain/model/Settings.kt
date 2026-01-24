package com.tomnylow.flipword.domain.model

data class Settings(
    val nativeLanguage: Language = SettingsDefaults.nativeLanguage,
    val learningLanguage: Language = SettingsDefaults.learningLanguage,
    val theme: AppTheme = SettingsDefaults.theme,
    val notificationsEnabled: Boolean = SettingsDefaults.notificationsEnabled,
    val notificationHour: Int = SettingsDefaults.notificationHour,
    val notificationMinute: Int = SettingsDefaults.notificationMinute
) {
    companion object SettingsDefaults {
        val nativeLanguage: Language = Language.RUSSIAN
        val learningLanguage: Language = Language.ENGLISH
        val theme: AppTheme = AppTheme.SYSTEM
        val notificationsEnabled: Boolean = false
        val notificationHour: Int = 19
        val notificationMinute: Int = 0
    }
}