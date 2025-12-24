package com.tomnylow.flipword.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tomnylow.flipword.ui.screens.deck_detail.DeckDetailScreen
import com.tomnylow.flipword.ui.screens.home.HomeScreen
import com.tomnylow.flipword.ui.screens.learn.LearnScreen
import com.tomnylow.flipword.ui.screens.profile.ProfileScreen
import com.tomnylow.flipword.ui.screens.repeat.RepeatScreen
import com.tomnylow.flipword.ui.screens.study.StudyScreen


@Composable
fun NavigationGraph(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen()
        }
        composable(BottomNavItem.Study.route) {
            StudyScreen(onDeckClick = {
                navController.navigate(Screen.DeckDetail.createRoute(it))
            })
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen()
        }
        composable(
            Screen.DeckDetail.route
        ) {
            DeckDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onLearnClick = { deckId ->
                    navController.navigate(Screen.Learn.createRoute(deckId))
                },
                onRepeatClick = { deckId ->
                    navController.navigate(Screen.Repeat.createRoute(deckId))
                },
                deckId = Screen.getDeckId(it.arguments)
            )
        }
        composable(
            Screen.Learn.route
        ) {
            LearnScreen(
                onNavigateBack = { navController.popBackStack() },
                deckId = Screen.getDeckId(it.arguments)
            )
        }
        composable(
            Screen.Repeat.route
        ) {
            RepeatScreen(onNavigateBack = { navController.popBackStack() },
                deckId = Screen.getDeckId(it.arguments))
        }
    }
}
