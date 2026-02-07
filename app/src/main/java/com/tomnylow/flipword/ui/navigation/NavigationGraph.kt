package com.tomnylow.flipword.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import com.tomnylow.flipword.ui.screens.onboarding.OnboardingScreen


@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onboardingCompleted: Boolean
) {
    val startRoute = remember {
        if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startRoute
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onRepeatWordsClick = { navController.navigate(Screen.Repeat.createRoute(null)) })
        }
        composable(Screen.Study.route) {
            DecksScreen(onDeckClick = {
                navController.navigate(Screen.DeckDetail.createRoute(it))
            })
        }
        composable(Screen.Profile.route) {
            ProfileScreen(onLoginClick = { navController.navigate(Screen.Auth.route) })
        }
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
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
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onSkipLoginClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                })
        }
    }
}
