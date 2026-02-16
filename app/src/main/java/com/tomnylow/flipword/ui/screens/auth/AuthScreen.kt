package com.tomnylow.flipword.ui.screens.auth

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tomnylow.flipword.R
import kotlinx.coroutines.delay

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun AuthScreen(
    onAuthFinish: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.message.collect { messageResId ->
            val message = context.getString(messageResId)
            snackbarHostState.showSnackbar(message)
            if (messageResId == R.string.auth_password_reset_email_sent) {
                delay(1000)
                showForgotPasswordDialog = false
            }
        }
    }

    LaunchedEffect(viewModel.currentUser) {
        viewModel.currentUser.collect { user ->
            if (user != null) onAuthFinish()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (state.mode) {
                        AuthMode.LOGIN -> stringResource(R.string.auth_login_title)
                        AuthMode.REGISTER -> stringResource(R.string.auth_register_title)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (state.mode == AuthMode.REGISTER) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.name,
                        onValueChange = viewModel::onNameChanged,
                        label = { Text(stringResource(R.string.user_name_prefix)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = { Text(stringResource(R.string.user_email_perfix)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.password,
                    onValueChange = viewModel::onPasswordChanged,
                    label = { Text(stringResource(R.string.user_password_prefix)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        when (state.mode) {
                            AuthMode.LOGIN -> viewModel.signIn()
                            AuthMode.REGISTER -> viewModel.signUp()
                        }
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(16.dp))
                    } else {
                        Text(
                            when (state.mode) {
                                AuthMode.LOGIN -> stringResource(R.string.auth_login_button)
                                AuthMode.REGISTER -> stringResource(R.string.auth_register_button)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = {
                    viewModel.toggleMode()
                }) {
                    Text(
                        if (state.mode == AuthMode.LOGIN) stringResource(R.string.auth_switch_to_register)
                        else stringResource(R.string.auth_switch_to_login)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (state.mode == AuthMode.LOGIN) {
                    TextButton(onClick = { showForgotPasswordDialog = true }) {
                        Text(stringResource(R.string.auth_forgot_password_button))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onAuthFinish) {
                    Text(stringResource(R.string.continue_logout_button))
                }
            }
        }
    )

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text(stringResource(R.string.recover_password_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.recover_password_dialog_text), textAlign = TextAlign.Center)
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChanged,
                        label = { Text(stringResource(R.string.user_email_perfix)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetPassword()
                    },
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(16.dp))
                    } else {
                        Text(stringResource(R.string.recover_password_confirm_button))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }
}
