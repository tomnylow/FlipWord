package com.tomnylow.flipword.data.local.session

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SessionPreferencesKeys {
    val USER_ID = stringPreferencesKey("user_id")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_DISPLAY_NAME = stringPreferencesKey("user_display_name")
    val TUTORIAL_FINISHED = booleanPreferencesKey("tutorial_finished")
}