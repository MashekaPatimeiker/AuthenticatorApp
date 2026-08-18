package com.example.authenticatorapp.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore("theme_preferences")

object ThemePreference {
    private val THEME_KEY = booleanPreferencesKey("dark_theme")

    suspend fun saveTheme(context: Context, isDark: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_KEY] = isDark
        }
    }

    fun getTheme(context: Context): Flow<Boolean> {
        return context.themeDataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }
}