package com.tomnylow.flipword.data.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tomnylow.flipword.domain.usecase.card.GetAllDueCardsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
@HiltWorker
class RepeatWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val getAllDueCardsUseCase: GetAllDueCardsUseCase,
    private val notificationsHelper: NotificationsHelper
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
       val dueCards = getAllDueCardsUseCase().first()
        if (dueCards.isNotEmpty()) {
            notificationsHelper.showRepeatWordsNotification(dueCards)
        }
        return Result.success()
    }

}