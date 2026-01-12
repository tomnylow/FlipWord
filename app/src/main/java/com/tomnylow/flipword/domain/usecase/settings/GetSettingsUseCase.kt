package com.tomnylow.flipword.domain.usecase.settings

import com.tomnylow.flipword.domain.model.Settings
import com.tomnylow.flipword.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Settings> = repository.settingsFlow
}