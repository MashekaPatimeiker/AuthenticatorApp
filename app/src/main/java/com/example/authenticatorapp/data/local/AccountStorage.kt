package com.example.authenticatorapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.authenticatorapp.domain.models.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

private val Context.dataStore by preferencesDataStore("accounts")

class AccountStorage(private val context: Context) {

    companion object {
        private val ACCOUNTS_KEY = stringPreferencesKey("accounts_list")
    }

    // Сохраняем список аккаунтов
    suspend fun saveAccounts(accounts: List<Account>) {
        val json = Json.encodeToString(accounts)
        context.dataStore.edit { preferences ->
            preferences[ACCOUNTS_KEY] = json
        }
    }

    // Получаем поток с аккаунтами
    fun getAccounts(): Flow<List<Account>> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[ACCOUNTS_KEY]
            if (json.isNullOrEmpty()) {
                emptyList()
            } else {
                try {
                    Json.decodeFromString(json)
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }
    }

    // Очистить все аккаунты
    suspend fun clearAccounts() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCOUNTS_KEY)
        }
    }
}