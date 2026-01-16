package com.tomnylow.flipword.domain.usecase.notifications

import androidx.work.WorkManager
import javax.inject.Inject

class StopRepeatWorkerUseCase @Inject constructor(
    private val workManager: WorkManager
) {
    operator fun invoke() {
        workManager.cancelUniqueWork("RepeatWords")
    }
}