package com.tomnylow.flipword.data.local.session

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.tomnylow.flipword.domain.model.Session
import com.tomnylow.flipword.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.dataStore
    val sessionFlow: Flow<Session> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> mapPreferencesToSession(preferences) }

    suspend fun saveUser(user: User) {
        Log.d("Session", "Saving $user")
        dataStore.edit { prefs ->
            prefs[SessionPreferencesKeys.USER_ID] = user.id
            prefs[SessionPreferencesKeys.USER_EMAIL] = user.email
            prefs[SessionPreferencesKeys.USER_DISPLAY_NAME] = user.displayName
        }
    }

    suspend fun clearUser() {
        dataStore.edit { prefs ->
            prefs.remove(SessionPreferencesKeys.USER_ID)
            prefs.remove(SessionPreferencesKeys.USER_EMAIL)
            prefs.remove(SessionPreferencesKeys.USER_DISPLAY_NAME)
        }
    }

    suspend fun setTutorialFinished(finished: Boolean) {
        dataStore.edit { prefs ->
            prefs[SessionPreferencesKeys.TUTORIAL_FINISHED] = finished
        }
    }

    private fun mapPreferencesToSession(prefs: Preferences): Session {
        val userId = prefs[SessionPreferencesKeys.USER_ID]
        val userEmail = prefs[SessionPreferencesKeys.USER_EMAIL]
        val displayName = prefs[SessionPreferencesKeys.USER_DISPLAY_NAME] ?: ""
        val tutorialFinished = prefs[SessionPreferencesKeys.TUTORIAL_FINISHED] ?: false

        val user = if (userId != null && userEmail != null) {
            User(id = userId, email = userEmail, displayName = displayName)
        } else {
            null
        }

        return Session(user = user, tutorialFinished = tutorialFinished)
    }
}