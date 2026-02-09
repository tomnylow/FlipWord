@file:OptIn(ExperimentalMaterial3Api::class)

package com.tomnylow.flipword.ui.screens.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tomnylow.flipword.R
import com.tomnylow.flipword.domain.model.AppTheme
import com.tomnylow.flipword.domain.model.Language

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLoginClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showExactAlarmButton by remember { mutableStateOf(false) }

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

    LaunchedEffect(state.settings.notificationsEnabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            showExactAlarmButton = state.settings.notificationsEnabled && !alarmManager.canScheduleExactAlarms()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { resId ->
            snackbarHostState.showSnackbar(context.getString(resId))
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        topBar = {
            TopAppBar({
                Text(stringResource(R.string.profile_screen_title))
            })
        },
        content = { paddingValues ->

            if (showTimePicker) {
                TimePickerDialog(
                    onDismiss = { showTimePicker = false },
                    onConfirm = { hour, minute ->
                        viewModel.updateNotificationsTime(hour, minute)
                        showTimePicker = false
                    },
                    initialHour = state.settings.notificationHour,
                    initialMinute = state.settings.notificationMinute
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp)

            ) {

                AccountInfoCard(
                    email = state.userEmail,
                    name = state.username,
                    onSignOutClick = viewModel::signOut,
                    onLoginClick = onLoginClick,
                    onPushClick = viewModel::pushDataBackup,
                    onFetchClick = viewModel::fetchDataBackup,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                        Text(
                            text = stringResource(R.string.settings_block_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        DropdownSettingItem(
                            title = stringResource(R.string.native_lang_setting),
                            value = state.settings.nativeLanguage.displayName,
                            items = Language.entries,
                            itemToString = { it.displayName },
                            onItemSelected = { viewModel.updateNativeLanguage(it) }
                        )
                        DropdownSettingItem(
                            title = stringResource(R.string.learning_lang_setting),
                            value = state.settings.learningLanguage.displayName,
                            items = Language.entries,
                            itemToString = { it.displayName },
                            onItemSelected = { viewModel.updateLearningLanguage(it) }
                        )

                        val currentTheme = when (state.settings.theme) {
                            AppTheme.SYSTEM -> stringResource(R.string.system_theme_name)
                            AppTheme.LIGHT -> stringResource(R.string.light_theme_name)
                            AppTheme.DARK -> stringResource(R.string.dark_theme_name)
                        }

                        DropdownSettingItem(
                            title = stringResource(R.string.app_theme_setting),
                            value = currentTheme,
                            items = AppTheme.entries,
                            itemToString = {
                                when(it) {
                                    AppTheme.SYSTEM -> context.getString(R.string.system_theme_name)
                                    AppTheme.LIGHT -> context.getString(R.string.light_theme_name)
                                    AppTheme.DARK -> context.getString(R.string.dark_theme_name)
                                }
                            },
                            onItemSelected = { viewModel.updateTheme(it) }
                        )

                        SwitchSettingItem(
                            title = stringResource(R.string.notif_setting),
                            checked = state.settings.notificationsEnabled,
                            onCheckedChange = { checked ->
                                if (!checked) {
                                    viewModel.updateNotificationsEnabled(false)
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.updateNotificationsEnabled(true)
                                    }
                                }
                            }
                        )
                        if (showExactAlarmButton) {
                            TextButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    val intent = Intent().apply {
                                        action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                        data = "package:${context.packageName}".toUri()
                                    }
                                    exactAlarmPermissionLauncher.launch(intent)
                                },
                                content = { Text(stringResource(R.string.allow_alarms_setting), style = MaterialTheme.typography.bodyLarge) })

                        }

                        if (state.settings.notificationsEnabled) {
                            TimeSettingItem(
                                title = stringResource(R.string.notif_time_setting),
                                hour = state.settings.notificationHour,
                                minute = state.settings.notificationMinute,
                                onClick = { showTimePicker = true }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AccountInfoCard(
    modifier: Modifier = Modifier,
    email: String?,
    name: String?,
    onSignOutClick: () -> Unit,
    onLoginClick: () -> Unit,
    onPushClick: () -> Unit,
    onFetchClick: () -> Unit
) {

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = stringResource(R.string.account_block_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (email != null) {

                UserInfoRow(
                    label = stringResource(R.string.user_name_prefix),
                    value = name ?: stringResource(R.string.user_name_not_found),
                    icon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(8.dp))

                UserInfoRow(
                    label = stringResource(R.string.user_email_perfix),
                    value = email,
                    icon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onPushClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.push_backup_button))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onFetchClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.fetch_backup_button))
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onSignOutClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.logout_button))
                }

            } else {

                Text(
                    text = stringResource(R.string.user_not_auth_message),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.login_button))
                }
            }
        }
    }
}

@Composable
fun UserInfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun TimeSettingItem(
    modifier: Modifier = Modifier,
    title: String,
    hour: Int,
    minute: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title)
        Text(
            text = String.format("%02d:%02d", hour, minute),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun <T> DropdownSettingItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    items: List<T>,
    itemToString: (T) -> String,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(title) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemToString(item)) },
                    onClick = {
                        expanded = false
                        onItemSelected(item)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SwitchSettingItem(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.timepicker_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(16.dp))

                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel_button))
                    }
                    TextButton(
                        onClick = {
                            onConfirm(timePickerState.hour, timePickerState.minute)
                        }
                    ) {
                        Text(stringResource(R.string.apply_button))
                    }
                }
            }
        }
    }
}