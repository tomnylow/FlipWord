package com.tomnylow.flipword.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.AppTheme
import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.domain.model.Settings
import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.usecase.settings.GetSettingsUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateAppThemeUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateLearningLanguageUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateNativeLanguageUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateNotificationTimeUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateNotificationsEnabledUseCase
import com.tomnylow.flipword.domain.usecase.user.UpdateNotificationScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateNativeLanguageUseCase: UpdateNativeLanguageUseCase,
    private val updateLearningLanguageUseCase: UpdateLearningLanguageUseCase,
    private val updateThemeUseCase: UpdateAppThemeUseCase,
    private val updateNotificationsEnabledUseCase: UpdateNotificationsEnabledUseCase,
    private val updateNotificationTimeUseCase: UpdateNotificationTimeUseCase,
    private val updateNotificationScheduleUseCase: UpdateNotificationScheduleUseCase
) : ViewModel() {

    val state: StateFlow<SettingsState> = getSettingsUseCase()
        .map { settings -> SettingsState(settings = settings) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsState()
        )

    fun updateNativeLanguage(language: Language) {
        viewModelScope.launch {
            updateNativeLanguageUseCase(language)
        }
    }

    fun updateLearningLanguage(language: Language) {
        viewModelScope.launch {
            updateLearningLanguageUseCase(language)
        }
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            updateThemeUseCase(theme)
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateNotificationsEnabledUseCase(enabled)
            updateNotificationScheduleUseCase(
                enabled,
                state.value.settings.notificationHour,
                state.value.settings.notificationMinute
            )
        }
    }

    fun updateNotificationsTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            updateNotificationTimeUseCase(hour, minute)
            updateNotificationScheduleUseCase(
                state.value.settings.notificationsEnabled,
                hour,
                minute
            )
        }
    }
}

data class SettingsState(
    val settings: Settings = Settings(),
    val user: User? = null
)
