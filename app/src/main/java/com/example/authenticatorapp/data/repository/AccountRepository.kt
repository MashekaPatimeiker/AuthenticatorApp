package com.example.authenticatorapp.data.repository

import com.example.authenticatorapp.data.network.ApiService
import com.example.authenticatorapp.domain.models.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AccountRepository(
    private val apiService: ApiService
) {
    fun getAllAccounts(): Flow<List<Account>> = flow {
        val accounts = apiService.getAccounts()
        emit(accounts)
    }

    suspend fun addAccount(account: Account) {
        apiService.createAccount(account)
    }

    suspend fun deleteAccount(account: Account) {
        apiService.deleteAccount(account.id.toInt())
    }
}