package com.tomnylow.flipword.domain.model

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromName(name: String): AppTheme = entries.find { it.name == name } ?: SYSTEM
    }

}