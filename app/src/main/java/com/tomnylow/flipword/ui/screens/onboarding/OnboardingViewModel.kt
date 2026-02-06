package com.tomnylow.flipword.ui.screens.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Settings
import com.tomnylow.flipword.domain.usecase.onboarding.SetOnboardingCompletedUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateNotificationsEnabledUseCase
import com.tomnylow.flipword.domain.usecase.user.UpdateNotificationScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
    private val updateNotificationsEnabledUseCase: UpdateNotificationsEnabledUseCase,
    private val updateNotificationScheduleUseCase: UpdateNotificationScheduleUseCase
) : ViewModel() {

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            setOnboardingCompletedUseCase(true)
        }
    }
    fun updateNotificationsEnabled(enabled: Boolean){
        viewModelScope.launch {
            updateNotificationsEnabledUseCase(enabled)
            updateNotificationScheduleUseCase(
                enabled,
                Settings.SettingsDefaults.notificationHour,
                Settings.SettingsDefaults.notificationMinute
            )
        }
    }
}
