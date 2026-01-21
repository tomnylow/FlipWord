package com.tomnylow.flipword.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tomnylow.flipword.ui.screens.auth.AuthScreen

import com.tomnylow.flipword.ui.screens.deck_detail.DeckDetailScreen
import com.tomnylow.flipword.ui.screens.home.HomeScreen
import com.tomnylow.flipword.ui.screens.learn.LearnScreen
import com.tomnylow.flipword.ui.screens.profile.ProfileScreen
import com.tomnylow.flipword.ui.screens.repeat.RepeatScreen
import com.tomnylow.flipword.ui.screens.decks.DecksScreen


@Composable
fun NavigationGraph(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(onRepeatWordsClick = { navController.navigate(Screen.Repeat.createRoute(null)) })
        }
        composable(BottomNavItem.Study.route) {
            DecksScreen(onDeckClick = {
                navController.navigate(Screen.DeckDetail.createRoute(it))
            })
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                onLoginClick = { navController.navigate(Screen.Auth.route)}
            )
        }
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthFinish = { navController.navigate(Screen.Home.route) }
            )
        }

        composable(
            Screen.DeckDetail.route,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) {
            DeckDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onLearnClick = { deckId ->
                    navController.navigate(Screen.Learn.createRoute(deckId))
                },
                onRepeatClick = { deckId ->
                    navController.navigate(Screen.Repeat.createRoute(deckId))
                }
            )
        }
        composable(
            Screen.Learn.route,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) {
            LearnScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.Repeat.route,
            arguments = listOf(navArgument("deckId") {
                type = NavType.StringType
                nullable = true
            })
        ) {
            RepeatScreen(onNavigateBack = { navController.popBackStack() })
        }

    }
}
