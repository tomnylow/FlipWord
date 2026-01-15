package com.tomnylow.flipword.domain.model

data class Settings(
    val nativeLanguage: Language = Language.RUSSIAN,
    val learningLanguage: Language = Language.ENGLISH,
    val theme: AppTheme = AppTheme.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val notificationHour: Int = 19,
    val notificationMinute: Int = 0

)

enum class AppTheme(val displayName: String) {
    SYSTEM("Как в системе"),
    LIGHT("Светлая"),
    DARK("Темная");

    companion object {
        fun fromName(name: String): AppTheme = entries.find { it.name == name } ?: SYSTEM
    }

}

enum class Language(val code: String, val displayName: String) {
    RUSSIAN("ru", "Русский"),
    ENGLISH("en", "English"),
    FRENCH("fr", "Français");

    companion object {
        fun fromCode(code: String): Language = entries.find { it.code == code } ?: ENGLISH
    }

}