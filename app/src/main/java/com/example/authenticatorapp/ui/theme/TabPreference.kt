package com.example.authenticatorapp.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.tabDataStore by preferencesDataStore("tab_preferences")

object TabPreference {
    private val TAB_KEY = intPreferencesKey("selected_tab")

    suspend fun saveTab(context: Context, tabIndex: Int) {
        context.tabDataStore.edit { preferences ->
            preferences[TAB_KEY] = tabIndex
        }
    }

    suspend fun getTab(context: Context): Int {
        return context.tabDataStore.data.map { preferences ->
            preferences[TAB_KEY] ?: 0
        }.first()
    }
}