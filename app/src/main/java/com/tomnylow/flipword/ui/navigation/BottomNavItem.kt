package com.tomnylow.flipword.ui.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem(Screen.Home.route, "Главная", Icons.Default.Home)
    object Study : BottomNavItem(Screen.Study.route, "Учеба", Icons.Default.Email)
    object Profile : BottomNavItem(Screen.Profile.route, "Профиль", Icons.Default.AccountBox)
}
