package com.tomnylow.flipword.ui.screens.onboarding

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tomnylow.flipword.R

private data class OnboardingPageInfo(
    val image: ImageVector,
    val title: String,
    val description: String,
    val content: @Composable (OnboardingViewModel) -> Unit = {}
)

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onSkipLoginClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val pages = getOnboardingPages(onLoginClick, onSkipLoginClick)
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            OnboardingPageContent(pages[pageIndex], viewModel)
        }

        AnimatedVisibility(visible = pagerState.currentPage < pages.size - 1) {
            TextButton(
                onClick = {
                    viewModel.setOnboardingCompleted()
                    onSkipLoginClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(stringResource(R.string.onboarding_skip))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPageInfo, viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = page.image,
            contentDescription = null,
            modifier = Modifier.size(128.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        page.content(viewModel)
    }
}

@Composable
private fun getOnboardingPages(
    onLoginClick: () -> Unit,
    onSkipLoginClick: () -> Unit
): List<OnboardingPageInfo> {
    val notificationsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    val exactAlarmPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )
    val context = LocalContext.current

    return listOf(
        OnboardingPageInfo(
            image = Icons.AutoMirrored.Filled.LibraryBooks,
            title = stringResource(R.string.onboarding_cards_title),
            description = stringResource(R.string.onboarding_cards_text)
        ),
        OnboardingPageInfo(
            image = Icons.Default.NotificationsActive,
            title = stringResource(R.string.onboarding_notif_title),
            description = stringResource(R.string.onboarding_notif_text),
            content = { viewModel ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.updateNotificationsEnabled(true)
                            }
                        }
                    ) {
                        Text(stringResource(R.string.onboarding_allow_notif_button))
                    }
                    OutlinedButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                if (!alarmManager.canScheduleExactAlarms()) {
                                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                        data = "package:${context.packageName}".toUri()
                                    }
                                    exactAlarmPermissionLauncher.launch(intent)
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.onboarding_allow_alarms_button))
                    }
                    Text(
                        text = stringResource(R.string.onboarding_notif_note),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        ),
        OnboardingPageInfo(
            image = Icons.Default.Backup,
            title = stringResource(R.string.onboarding_account_title),
            description = stringResource(R.string.onboarding_account_text),
            content = { viewModel ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            viewModel.setOnboardingCompleted()
                            onLoginClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.onboarding_goto_login_button))
                    }
                    TextButton(
                        onClick = {
                            viewModel.setOnboardingCompleted()
                            onSkipLoginClick()
                        }
                    ) {
                        Text(stringResource(R.string.continue_logout_button))
                    }
                }
            }
        )
    )
}