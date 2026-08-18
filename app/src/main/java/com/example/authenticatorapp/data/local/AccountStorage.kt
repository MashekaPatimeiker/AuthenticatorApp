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

private val Context.accountDataStore by preferencesDataStore("account_preferences")

class AccountStorage(private val context: Context) {

    companion object {
        private val ACCOUNTS_KEY = stringPreferencesKey("accounts_list")
    }

    suspend fun saveAccounts(accounts: List<Account>) {
        val json = Json.encodeToString(accounts)
        context.accountDataStore.edit { preferences ->
            preferences[ACCOUNTS_KEY] = json
        }
    }

    fun getAccounts(): Flow<List<Account>> {
        return context.accountDataStore.data.map { preferences ->
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

    suspend fun clearAccounts() {
        context.accountDataStore.edit { preferences ->
            preferences.remove(ACCOUNTS_KEY)
        }
    }
}