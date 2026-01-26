package com.tomnylow.flipword.domain.model

data class Session (
    val user: User? = null,
    val tutorialFinished: Boolean = false
)