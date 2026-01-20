package com.tomnylow.flipword.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    onAuthFinish: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.message.collect { message ->
            snackbarHostState.showSnackbar(message)
            if (message.contains("Письмо отправлено")) {
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
                        AuthMode.LOGIN -> "Вход"
                        AuthMode.REGISTER -> "Регистрация"
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
                        label = { Text("Имя") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.password,
                    onValueChange = viewModel::onPasswordChanged,
                    label = { Text("Пароль") },
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
                                AuthMode.LOGIN -> "Войти"
                                AuthMode.REGISTER -> "Зарегистрироваться"
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = {
                    viewModel.toggleMode()
                }) {
                    Text(
                        if (state.mode == AuthMode.LOGIN) "Нет аккаунта? Регистрация"
                        else "Уже есть аккаунт? Вход"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (state.mode == AuthMode.LOGIN) {
                    TextButton(onClick = { showForgotPasswordDialog = true }) {
                        Text("Забыли пароль?")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onAuthFinish) {
                    Text("Продолжить без аккаунта")
                }
            }
        }
    )

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Восстановить пароль") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Введите email, указанный при регистрации", textAlign = TextAlign.Center)
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChanged,
                        label = { Text("Email") },
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
                        Text("Отправить")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}