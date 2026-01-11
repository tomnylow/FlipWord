package com.tomnylow.flipword.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.tomnylow.flipword.domain.model.Settings
import com.tomnylow.flipword.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()
}

data class SettingsState(
    val settings: Settings,
    val user: User? = null
)
