package com.tomnylow.flipword.ui.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.tomnylow.flipword.R

sealed class BottomNavItem(val route: String, val title: @Composable () -> String, val icon: ImageVector) {
    object Home : BottomNavItem(Screen.Home.route, { stringResource(id = R.string.bottom_nav_home) }, Icons.Default.Home)
    object Study : BottomNavItem(Screen.Study.route, { stringResource(id = R.string.bottom_nav_study) }, Icons.Default.AutoAwesomeMotion)
    object Profile : BottomNavItem(Screen.Profile.route, { stringResource(id = R.string.bottom_nav_profile) }, Icons.Default.AccountBox)

}
