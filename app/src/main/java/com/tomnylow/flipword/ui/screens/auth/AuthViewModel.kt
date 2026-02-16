package com.tomnylow.flipword.ui.screens.auth

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.tomnylow.flipword.R
import com.tomnylow.flipword.domain.usecase.user.GetUserUseCase
import com.tomnylow.flipword.domain.usecase.user.SendPasswordResetUseCase
import com.tomnylow.flipword.domain.usecase.user.SignInUseCase
import com.tomnylow.flipword.domain.usecase.user.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _message = MutableSharedFlow<Int>()
    val message: SharedFlow<Int> = _message.asSharedFlow()

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
            viewModelScope.launch { _message.emit(R.string.auth_invalid_email) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = signInUseCase(email, password)
            _state.update { it.copy(isLoading = false) }
            result.exceptionOrNull()?.let {
                viewModelScope.launch { _message.emit(processAuthException(it)) }
            }
        }
    }

    fun signUp() {
        if (_state.value.mode != AuthMode.REGISTER) return
        val name = _state.value.name
        val email = _state.value.email
        val password = _state.value.password
        if (name.isBlank() || !isValidEmail(email)) {
            viewModelScope.launch { _message.emit(R.string.auth_fill_all_fields_correctly) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = signUpUseCase(name, email, password)
            _state.update { it.copy(isLoading = false) }
            result.exceptionOrNull()?.let {
                viewModelScope.launch { _message.emit(processAuthException(it)) }

            }
        }
    }

    fun resetPassword() {
        val email = _state.value.email
        if (!isValidEmail(email)) {
            viewModelScope.launch { _message.emit(R.string.auth_invalid_email) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = sendPasswordResetEmailUseCase(email)
            _state.update { it.copy(isLoading = false) }
            result.exceptionOrNull()?.let {
                viewModelScope.launch { _message.emit(processAuthException(it)) }
            } ?: viewModelScope.launch { _message.emit(R.string.auth_password_reset_email_sent) }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    @StringRes
    private fun processAuthException(error: Throwable): Int {
        return when (error) {
            is FirebaseAuthWeakPasswordException -> R.string.auth_weak_password
            is FirebaseAuthInvalidCredentialsException -> {
                when (error.errorCode) {
                    "ERROR_INVALID_EMAIL" -> R.string.auth_invalid_email_format
                    "ERROR_WRONG_PASSWORD" -> R.string.auth_wrong_password
                    "ERROR_USER_DISABLED" -> R.string.auth_account_disabled
                    else -> R.string.auth_invalid_credentials
                }
            }
            is FirebaseAuthUserCollisionException -> R.string.auth_user_already_exists
            is FirebaseNetworkException -> R.string.auth_no_internet
            is FirebaseApiNotAvailableException -> R.string.auth_google_services_unavailable
            is java.net.UnknownHostException,
            is java.net.SocketTimeoutException,
            is java.io.IOException -> R.string.auth_network_error
            else -> {
                error.message?.let { msg ->
                    when {
                        msg.contains("TOO_MANY_ATTEMPTS_TRY_LATER", ignoreCase = true) -> R.string.auth_too_many_attempts
                        msg.contains("OPERATION_NOT_ALLOWED", ignoreCase = true) -> R.string.auth_operation_not_allowed
                        msg.contains("INVALID_CUSTOM_TOKEN", ignoreCase = true) -> R.string.auth_invalid_custom_token
                        msg.contains("CREDENTIAL_MISMATCH", ignoreCase = true) -> R.string.auth_credential_mismatch
                        msg.contains("API_NOT_AVAILABLE", ignoreCase = true) -> R.string.auth_google_services_unavailable
                        else -> R.string.auth_unknown_error_no_message
                    }
                } ?: R.string.auth_unknown_error_no_message
            }
        }
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