package dev.pastukhov.booking.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.pastukhov.booking.domain.model.AppLanguage
import dev.pastukhov.booking.domain.model.AppTheme
import dev.pastukhov.booking.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

/**
 * Repository for user settings using DataStore.
 */
@Singleton
class UserSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val THEME = stringPreferencesKey("theme")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = stringPreferencesKey("user_id")
    }

    val userSettings: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            language = AppLanguage.fromCode(
                preferences[PreferencesKeys.LANGUAGE] ?: AppLanguage.ENGLISH.code
            ),
            theme = AppTheme.valueOf(
                preferences[PreferencesKeys.THEME] ?: AppTheme.SYSTEM.name
            ),
            notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS] ?: true
        )
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language.code
        }
        applyLanguage(language)
    }

    private fun applyLanguage(language: AppLanguage) {
        val locale = when (language) {
            AppLanguage.ENGLISH -> "en"
            AppLanguage.SPANISH -> "es"
        }
        val appLocale = LocaleListCompat.forLanguageTags(locale)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS] = enabled
        }
    }

    suspend fun clearSettings() {
        context.dataStore.edit { it.clear() }
    }

    /**
     * Save authentication token for splash screen auth check.
     */
    suspend fun saveAuthToken(token: String, userId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN] = token
            preferences[PreferencesKeys.USER_ID] = userId
        }
    }

    /**
     * Check if user has valid auth token.
     * Returns Flow to observe changes.
     */
    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        val token = preferences[PreferencesKeys.AUTH_TOKEN]
        !token.isNullOrEmpty()
    }

    /**
     * Get stored auth token synchronously for initial splash check.
     */
    suspend fun getAuthToken(): String? {
        var token: String? = null
        context.dataStore.data.collect { preferences ->
            token = preferences[PreferencesKeys.AUTH_TOKEN]
        }
        return token
    }

    /**
     * Clear authentication data (logout).
     */
    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.AUTH_TOKEN)
            preferences.remove(PreferencesKeys.USER_ID)
        }
    }
}
