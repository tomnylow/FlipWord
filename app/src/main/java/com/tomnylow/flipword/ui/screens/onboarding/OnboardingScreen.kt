package com.tomnylow.flipword.ui.screens.onboarding

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tomnylow.flipword.R
import com.tomnylow.flipword.ui.icons.customIcons
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onSkipLoginClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val context = LocalContext.current
    val animationScope = rememberCoroutineScope()

    val notificationsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isEnabled -> viewModel.updateNotificationsEnabled(isEnabled) }
    )
    val exactAlarmLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    val alarmManager = remember { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager.canScheduleExactAlarms()
    } else true

    val pages = remember {
        listOf(
            OnboardingPage.Welcome,
            OnboardingPage.Notifications(
                onPermissionRequest = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        viewModel.updateNotificationsEnabled(true)
                    }
                },
                onAlarmRequest = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = "package:${context.packageName}".toUri()
                        }
                        exactAlarmLauncher.launch(intent)
                    }
                },
                canScheduleAlarms = canScheduleExact
            ),
            OnboardingPage.Final(
                onLogin = {
                    viewModel.setOnboardingCompleted()
                    onLoginClick()
                },
                onSkip = {
                    viewModel.setOnboardingCompleted()
                    onSkipLoginClick()
                }
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) { pageIndex ->
            OnboardingPageContent(pages[pageIndex])
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    animationScope.launch {
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage - 1,
                            animationSpec = tween(durationMillis = 300)
                        )
                    }
                },
                enabled = pagerState.currentPage > 0
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.go_back_description))
            }

            Row(horizontalArrangement = Arrangement.Center) {
                repeat(pages.size) { index ->
                    val color = if (pagerState.currentPage == index)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            IconButton(
                onClick = {
                    animationScope.launch {
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage + 1,
                            animationSpec = tween(durationMillis = 300)
                        )
                    }
                },
                enabled = pagerState.currentPage < pages.size - 1
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.go_forward_button)
                )
            }
        }

        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onClick = {
                viewModel.setOnboardingCompleted()
                onSkipLoginClick()
            },
            enabled = pagerState.currentPage < pages.size - 1
        ) {
            Text(stringResource(R.string.onboarding_skip))
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val title = when (page) {
        is OnboardingPage.Welcome -> stringResource(R.string.onboarding_cards_title)
        is OnboardingPage.Notifications -> stringResource(R.string.onboarding_notif_title)
        is OnboardingPage.Final -> stringResource(R.string.onboarding_account_title)
    }

    val description = when (page) {
        is OnboardingPage.Welcome -> stringResource(R.string.onboarding_cards_text)
        is OnboardingPage.Notifications -> stringResource(R.string.onboarding_notif_text)
        is OnboardingPage.Final -> stringResource(R.string.onboarding_account_text)
    }

    val icon = when (page) {
        is OnboardingPage.Welcome -> customIcons.TablerCards
        is OnboardingPage.Notifications -> Icons.Default.NotificationsActive
        is OnboardingPage.Final -> Icons.Default.Backup
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(128.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Spacer(Modifier.height(32.dp))
        Text(text = title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Text(text = description, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)

        Spacer(Modifier.height(32.dp))

        when (page) {
            is OnboardingPage.Notifications -> {
                Button(onClick = page.onPermissionRequest) {
                    Text(stringResource(R.string.onboarding_allow_notif_button))
                }
                if (!page.canScheduleAlarms) {
                    OutlinedButton(onClick = page.onAlarmRequest) {
                        Text(stringResource(R.string.onboarding_allow_alarms_button))
                    }
                }
            }
            is OnboardingPage.Final -> {
                Button(onClick = page.onLogin, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.onboarding_goto_login_button))
                }
                TextButton(onClick = page.onSkip) {
                    Text(stringResource(R.string.continue_logout_button))
                }
            }
            else -> {}
        }
    }
}
sealed class OnboardingPage {
    data object Welcome : OnboardingPage()

    data class Notifications(
        val onPermissionRequest: () -> Unit,
        val onAlarmRequest: () -> Unit,
        val canScheduleAlarms: Boolean
    ) : OnboardingPage()

    data class Final(
        val onLogin: () -> Unit,
        val onSkip: () -> Unit
    ) : OnboardingPage()
}
