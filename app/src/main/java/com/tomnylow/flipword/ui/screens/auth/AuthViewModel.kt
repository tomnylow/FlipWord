package com.tomnylow.flipword.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.usecase.user.GetUserUseCase
import com.tomnylow.flipword.domain.usecase.user.SendPasswordResetUseCase
import com.tomnylow.flipword.domain.usecase.user.SignInUseCase
import com.tomnylow.flipword.domain.usecase.user.SignOutUseCase
import com.tomnylow.flipword.domain.usecase.user.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetUseCase,
    private val signOutUseCase: SignOutUseCase,
    getUserUseCase: GetUserUseCase
) : ViewModel() {

    val currentUser = getUserUseCase()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            signInUseCase(email, password)
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            signUpUseCase(name, email, password)
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            sendPasswordResetEmailUseCase(email)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }
}