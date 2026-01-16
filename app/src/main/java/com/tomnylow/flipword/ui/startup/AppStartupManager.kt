package com.tomnylow.flipword.ui.startup

import com.tomnylow.flipword.domain.usecase.notifications.StartRepeatWorkerUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppStartupManager @Inject constructor(
    private val startRepeatWorkerUseCase: StartRepeatWorkerUseCase
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun startRepeatWorker(){
        scope.launch {
            startRepeatWorkerUseCase()
        }
    }
}