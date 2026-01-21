package com.tomnylow.flipword.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.tomnylow.flipword.MainActivity
import com.tomnylow.flipword.R
import com.tomnylow.flipword.domain.model.Card
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationsHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationsManager = context.getSystemService<NotificationManager>()

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            REPEAT_CHANNEL_ID, "Повторение слов",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationsManager?.createNotificationChannel(channel)

    }

    fun showRepeatWordsNotification(dueCards: List<Card>) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent =
            PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, REPEAT_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Пора повторить слова")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setContentText("Вас ждут ${dueCards.size} слов для повторения, возвращайтесь!")
            .build()
        notificationsManager?.notify(REPEAT_NOTIFICATION_ID, notification)
    }
    companion object {
        const val REPEAT_CHANNEL_ID = "words_repeat_channel"
        const val REPEAT_NOTIFICATION_ID = 101
    }
}