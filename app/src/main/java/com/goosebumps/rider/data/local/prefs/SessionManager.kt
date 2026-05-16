package com.goosebumps.rider.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rider_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val KEY_TOKEN = stringPreferencesKey("auth_token")
        val KEY_RIDER_ID = stringPreferencesKey("rider_id")
        val KEY_RIDER_NAME = stringPreferencesKey("rider_name")
        val KEY_IS_ONLINE = booleanPreferencesKey("is_online")
        val KEY_LANGUAGE = stringPreferencesKey("language")
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        val KEY_VOICE_NAV = booleanPreferencesKey("voice_navigation")
        val KEY_BATTERY_SAVER = booleanPreferencesKey("battery_saver")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
    }

    // Synchronous token read for OkHttp interceptor
    fun getToken(): String? = runBlocking {
        dataStore.data.first()[KEY_TOKEN]
    }

    fun isLoggedIn(): Boolean = getToken() != null

    val tokenFlow: Flow<String?> = dataStore.data.map { it[KEY_TOKEN] }
    val isOnlineFlow: Flow<Boolean> = dataStore.data.map { it[KEY_IS_ONLINE] ?: false }
    val languageFlow: Flow<String> = dataStore.data.map { it[KEY_LANGUAGE] ?: "en" }
    val batterySaverFlow: Flow<Boolean> = dataStore.data.map { it[KEY_BATTERY_SAVER] ?: false }
    val voiceNavFlow: Flow<Boolean> = dataStore.data.map { it[KEY_VOICE_NAV] ?: true }

    suspend fun saveSession(token: String, riderId: String, riderName: String) {
        dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_RIDER_ID] = riderId
            prefs[KEY_RIDER_NAME] = riderName
        }
    }

    suspend fun setOnlineStatus(isOnline: Boolean) {
        dataStore.edit { it[KEY_IS_ONLINE] = isOnline }
    }

    suspend fun setLanguage(lang: String) {
        dataStore.edit { it[KEY_LANGUAGE] = lang }
    }

    suspend fun setBatterySaver(enabled: Boolean) {
        dataStore.edit { it[KEY_BATTERY_SAVER] = enabled }
    }

    suspend fun setVoiceNav(enabled: Boolean) {
        dataStore.edit { it[KEY_VOICE_NAV] = enabled }
    }

    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }
}
