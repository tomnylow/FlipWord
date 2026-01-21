package com.tomnylow.flipword.ui.screens.profile

import android.os.Message
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.tomnylow.flipword.domain.model.AppTheme
import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.domain.model.Settings
import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.usecase.external.FetchBackupUseCase
import com.tomnylow.flipword.domain.usecase.external.PushBackupUseCase
import com.tomnylow.flipword.domain.usecase.settings.GetSettingsUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateAppThemeUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateLearningLanguageUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateNativeLanguageUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateNotificationTimeUseCase
import com.tomnylow.flipword.domain.usecase.settings.UpdateNotificationsEnabledUseCase
import com.tomnylow.flipword.domain.usecase.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateNativeLanguageUseCase: UpdateNativeLanguageUseCase,
    private val updateLearningLanguageUseCase: UpdateLearningLanguageUseCase,
    private val updateThemeUseCase: UpdateAppThemeUseCase,
    private val updateNotificationsEnabledUseCase: UpdateNotificationsEnabledUseCase,
    private val updateNotificationTimeUseCase: UpdateNotificationTimeUseCase,
    private val fetchBackupUseCase: FetchBackupUseCase,
    private val pushBackupUseCase: PushBackupUseCase,
    getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _snackbarMessage = MutableSharedFlow<String>()
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
        }
    }

    fun updateNotificationsTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            updateNotificationTimeUseCase(hour, minute)
        }
    }

    fun pushDataBackup() {
        viewModelScope.launch {
            //_state.update { it.copy(isLoading = true) }
            val result = pushBackupUseCase()
            //_state.update { it.copy(isLoading = false) }
            result.exceptionOrNull()?.let {
                viewModelScope.launch { _snackbarMessage.emit(processBackupException(it) ) }
            } ?: _snackbarMessage.emit( "Успешно отправлено в облако" )
        }
    }
    fun fetchDataBackup() {
        viewModelScope.launch {
            //_state.update { it.copy(isLoading = true) }
            val result = fetchBackupUseCase()
            //_state.update { it.copy(isLoading = false) }
            result.exceptionOrNull()?.let {
                viewModelScope.launch { _snackbarMessage.emit(processBackupException(it) ) }
            } ?: _snackbarMessage.emit( "Успешно загружено из облака" )
        }
    }
    private fun processBackupException(exception: Throwable): String {
        return when (exception) {

            is IllegalStateException -> "Вы не авторизованы. Пожалуйста, войдите в аккаунт."

            is FirebaseFirestoreException -> when (exception.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> "Сервер недоступен (оффлайн). Проверьте интернет."
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Доступ запрещен. Попробуйте выйти и войти снова."
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> "Превышена квота использования базы."
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> "Бэкап слишком большой (>1 МБ) или содержит неверные данные."
                else -> "Ошибка облака: ${exception.code}"
            }

            is IOException -> "Нет подключения к интернету."

            is SerializationException -> "Не удалось обработать данные для отправки."

            else -> "Произошла ошибка: ${exception.localizedMessage ?: "Неизвестная причина"}"
        }
    }
}

data class SettingsState(
    val settings: Settings = Settings(),
    val username: String? = null,
    val userEmail: String? = null,
)
