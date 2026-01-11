package com.tomnylow.flipword.domain.model

data class Settings(
    val nativeLanguage: Language = Language.RUSSIAN,
    val learningLanguage: Language = Language.ENGLISH,
    val theme: Theme = Theme.SYSTEM,
    val notificationsEnabled: Boolean = true
)

enum class Theme(val displayName: String) {
    SYSTEM("Как в системе"),
    LIGHT("Светлая"),
    DARK("Темная")
}

enum class Language(val code: String, val displayName: String) {
    RUSSIAN("ru", "Русский"),
    ENGLISH("en", "English"),
    FRENCH("fr", "Français")

}