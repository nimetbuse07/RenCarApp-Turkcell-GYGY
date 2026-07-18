package com.nimetatila.rencarapp_turkcell_gygy.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

@Singleton
class ThemePreferenceRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val THEME_KEY = booleanPreferencesKey("is_dark_theme")
    }

    /**
     * Flow emitting the saved theme choice (true for dark, false for light).
     */
    val isDarkTheme: Flow<Boolean> = context.themeDataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: false
    }

    /**
     * Persists the user's theme selection.
     */
    suspend fun setDarkTheme(isDark: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_KEY] = isDark
        }
    }
}
