package com.tomnylow.flipword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Settings
import com.tomnylow.flipword.domain.usecase.onboarding.GetOnboardingCompletedUseCase
import com.tomnylow.flipword.domain.usecase.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase
) : ViewModel() {

    val settings: StateFlow<Settings> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Settings()
        )

    val onboardingCompleted: StateFlow<Boolean?> = getOnboardingCompletedUseCase()
        .map { it as Boolean? } // TODO: Получать не здесь
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}
