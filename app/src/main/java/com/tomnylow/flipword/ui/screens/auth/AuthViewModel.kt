package com.tomnylow.flipword.ui.screens.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.usecase.user.GetUserUseCase
import com.tomnylow.flipword.domain.usecase.user.SendPasswordResetUseCase
import com.tomnylow.flipword.domain.usecase.user.SignInUseCase
import com.tomnylow.flipword.domain.usecase.user.SignUpUseCase
import com.tomnylow.flipword.ui.screens.profile.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message.asSharedFlow()

    val currentUser = getUserUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun toggleMode() {
        _state.update {
            it.copy(mode = if (it.mode == AuthMode.REGISTER) AuthMode.LOGIN else AuthMode.REGISTER)
        }
    }

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onNameChanged(name: String) {
        _state.update { it.copy(name = name) }
    }


    fun signIn() {
        if (_state.value.mode != AuthMode.LOGIN) return
        val email = _state.value.email
        val password = _state.value.password
        if (!isValidEmail(email)) {
            viewModelScope.launch { _message.emit("Неверный email") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = signInUseCase(email, password)
            _state.update { it.copy(isLoading = false) }
            result.exceptionOrNull()?.let {
                viewModelScope.launch { _message.emit(processAuthException(it) ?: "Ошибка входа") }
            }
        }
    }

    fun signUp() {
        if (_state.value.mode != AuthMode.REGISTER) return
        val name = _state.value.name
        val email = _state.value.email
        val password = _state.value.password
        if (name.isBlank() || !isValidEmail(email)) {
            viewModelScope.launch { _message.emit("Заполните все поля корректно") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = signUpUseCase(name, email, password)
            _state.update { it.copy(isLoading = false) }
            result.exceptionOrNull()?.let {
                viewModelScope.launch { _message.emit(processAuthException(it) ?: "Ошибка регистрации") }

            }
        }
    }

    fun resetPassword() {
        val email = _state.value.email
        if (!isValidEmail(email)) {
            viewModelScope.launch { _message.emit("Неверный email") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = sendPasswordResetEmailUseCase(email)
            _state.update { it.copy(isLoading = false) }
            result.exceptionOrNull()?.let {
                viewModelScope.launch { _message.emit(processAuthException(it) ?: "Ошибка регистрации") }
            } ?: viewModelScope.launch { _message.emit("Письмо отправлено") }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun processAuthException(error: Throwable): String? {
        return "jjjj"
    }
}

enum class AuthMode { LOGIN, REGISTER }

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val isLoading: Boolean = false,
)