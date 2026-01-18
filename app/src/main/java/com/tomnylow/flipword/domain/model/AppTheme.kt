package com.tomnylow.flipword.domain.model

enum class AppTheme(val displayName: String) {
    SYSTEM("Как в системе"),
    LIGHT("Светлая"),
    DARK("Темная");

    companion object {
        fun fromName(name: String): AppTheme = entries.find { it.name == name } ?: SYSTEM
    }

}