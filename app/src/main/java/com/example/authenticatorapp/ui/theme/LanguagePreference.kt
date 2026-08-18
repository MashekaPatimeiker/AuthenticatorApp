package com.example.authenticatorapp.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.languageDataStore by preferencesDataStore("language_preferences")

object LanguagePreference {
    private val LANGUAGE_KEY = stringPreferencesKey("selected_language")

    suspend fun saveLanguage(context: Context, languageCode: String) {
        context.languageDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }

    fun getLanguage(context: Context): Flow<String> {
        return context.languageDataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: "ru"
        }
    }
}