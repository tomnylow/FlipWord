package com.tomnylow.flipword.ui.screens.profile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.tomnylow.flipword.R
import com.tomnylow.flipword.domain.model.AppTheme
import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.domain.model.Settings
import com.tomnylow.flipword.domain.usecase.external.FetchBackupUseCase
import com.tomnylow.flipword.domain.usecase.external.PushBackupUseCase
import com.tomnylow.flipword.domain.usecase.settings.GetSettingsUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateAppThemeUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateLearningLanguageUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateNativeLanguageUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateNotificationTimeUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateNotificationsEnabledUseCase
import com.tomnylow.flipword.domain.usecase.user.UpdateNotificationScheduleUseCase
import com.tomnylow.flipword.domain.usecase.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateNativeLanguageUseCase: UpdateNativeLanguageUseCase,
    private val updateLearningLanguageUseCase: UpdateLearningLanguageUseCase,
    private val updateThemeUseCase: UpdateAppThemeUseCase,
    private val updateNotificationsEnabledUseCase: UpdateNotificationsEnabledUseCase,
    private val updateNotificationTimeUseCase: UpdateNotificationTimeUseCase,
    private val updateNotificationScheduleUseCase: UpdateNotificationScheduleUseCase,
    private val fetchBackupUseCase: FetchBackupUseCase,
    private val pushBackupUseCase: PushBackupUseCase,
    getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _snackbarMessage = MutableSharedFlow<Int>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    val state: StateFlow<SettingsState> = combine(
        getSettingsUseCase(),
        getUserUseCase()
    ) { settings, user ->
        SettingsState(
            settings = settings,
            username = user?.displayName,
            userEmail = user?.email
        )
    }
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

    fun pushDataBackup() {
        viewModelScope.launch {
            val result = pushBackupUseCase()
            result.exceptionOrNull()?.let {
                viewModelScope.launch { _snackbarMessage.emit(processBackupException(it)) }
            } ?: _snackbarMessage.emit(R.string.backup_push_success)
        }
    }

    fun fetchDataBackup() {
        viewModelScope.launch {
            val result = fetchBackupUseCase()
            result.exceptionOrNull()?.let {
                viewModelScope.launch { _snackbarMessage.emit(processBackupException(it)) }
            } ?: _snackbarMessage.emit(R.string.backup_fetch_success)
        }
    }

    @StringRes
    private fun processBackupException(exception: Throwable): Int {
        return when (exception) {
            is IllegalStateException -> R.string.backup_not_authorized
            is FirebaseFirestoreException -> when (exception.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> R.string.backup_server_unavailable
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> R.string.backup_permission_denied
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> R.string.backup_quota_exceeded
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> R.string.backup_invalid_argument
                else -> R.string.backup_cloud_error
            }
            is IOException -> R.string.backup_no_internet
            is SerializationException -> R.string.backup_serialization_error
            else -> R.string.backup_unknown_error
        }
    }
}

data class SettingsState(
    val settings: Settings = Settings(),
    val username: String? = null,
    val userEmail: String? = null,
)
