package com.tomnylow.flipword.data.notifications

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tomnylow.flipword.domain.usecase.card.GetAllDueCardsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import javax.inject.Inject
@HiltWorker
class ShowNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getAllDueCardsUseCase: GetAllDueCardsUseCase,
    private val notificationsHelper: NotificationsHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val cards = getAllDueCardsUseCase().first()

        if (cards.isNotEmpty())
            notificationsHelper.showRepeatWordsNotification(cards)

        return Result.success()
    }
}