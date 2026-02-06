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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


private data class OnboardingPage(
    val image: ImageVector,
    val title: String,
    val description: String,
    val content: @Composable (pagerState: PagerState) -> Unit
)

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onSkipLoginClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    val notificationsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { enabled ->
            viewModel.updateNotificationsEnabled(enabled)
        }
    )

    val exactAlarmPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.updateNotificationsEnabled(true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(
                page = when (page) {
                    0 -> OnboardingPage(
                        image = Icons.Default.LibraryBooks,
                        title = "Создавайте и организуйте",
                        description = "Легко создавайте карточки со словами, переводом и примерами с помощью автозаполнения. Организуйте их в персональные колоды для удобного обучения."
                    ) {}

                    1 -> OnboardingPage(
                        image = Icons.Default.Psychology,
                        title = "Умное обучение",
                        description = "Изучайте новые слова и повторяйте старые с помощью научного алгоритма интервальных повторений SM-2 для максимальной эффективности."
                    ) { state ->

                        val context = LocalContext.current

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
                                Text("Разрешить уведомления")
                            }

                            Button(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        val alarmManager =
                                            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                                        if (!alarmManager.canScheduleExactAlarms()) {
                                            val intent = Intent().apply {
                                                action =
                                                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                                data = "package:${context.packageName}".toUri()
                                            }
                                            exactAlarmPermissionLauncher.launch(intent)
                                        } else {
                                            viewModel.updateNotificationsEnabled(true)
                                        }
                                    } else {
                                        viewModel.updateNotificationsEnabled(true)
                                    }
                                }
                            ) {
                                Text("Напоминать в точное время")
                            }
                            Text(
                                text = "Настроить все эти параметры, а также время напоминаний всегда можно в профиле.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    else -> OnboardingPage(
                        image = Icons.Default.Backup,
                        title = "Сохраняйте свой прогресс",
                        description = "Переносите свои колоды между устройствами. Для этого войдите в аккаунт."
                    ) { state ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.setOnboardingCompleted()
                                    onLoginClick()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Войти в аккаунт")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = {
                                    viewModel.setOnboardingCompleted()
                                    onSkipLoginClick()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Продолжить без аккаунта")
                            }

                        }
                    }
                },
                pagerState = pagerState
            )
        }


        TextButton(
            onClick = {
                viewModel.setOnboardingCompleted()
                onSkipLoginClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Пропустить")
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage, pagerState: PagerState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = page.image,
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        page.content(pagerState)
    }
}