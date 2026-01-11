package com.tomnylow.flipword.ui.navigation

import android.os.Bundle

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Study : Screen("study")
    object Profile : Screen("profile")

    object DeckDetail : Screen("deck_detail/{deckId}") {

        fun createRoute(deckId: Long) = "deck_detail/$deckId"
    }

    object Learn : Screen("learn/{deckId}") {
        fun createRoute(deckId: Long) = "learn/$deckId"
    }

    object Repeat : Screen("repeat?deckId={deckId}") {
        fun createRoute(deckId: Long?) = if (deckId != null) "repeat?deckId=$deckId" else "repeat"
    }

    companion object {
        fun getDeckId(args: Bundle?): Long? {
            return args?.getString("deckId")?.toLongOrNull()
        }
    }
}
