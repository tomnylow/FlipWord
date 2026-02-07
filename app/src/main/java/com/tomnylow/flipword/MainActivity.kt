package com.tomnylow.flipword

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.FlipWordTheme
import com.tomnylow.flipword.ui.navigation.BottomNavItem
import com.tomnylow.flipword.ui.navigation.NavigationGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.onboardingCompleted.value == null
        }

        setContent {
            FlipWordTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val onboardingCompleted by viewModel.onboardingCompleted.collectAsState(initial = null)

                val mainScreens = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Study,
                    BottomNavItem.Profile
                )

                val showBottomBar = mainScreens.any { it.route == currentDestination?.route }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                mainScreens.forEach { item ->
                                    NavigationBarItem(
                                        icon = { Icon(item.icon, contentDescription = item.title()) },
                                        label = { Text(item.title()) },
                                        selected = currentDestination?.route == item.route,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPaddings ->
                    if (onboardingCompleted != null) {
                        NavigationGraph(
                            navController = navController,
                            modifier = Modifier.padding(bottom = innerPaddings.calculateBottomPadding()),
                            onboardingCompleted = onboardingCompleted == true
                        )
                    }
                }
            }
        }
    }
}
