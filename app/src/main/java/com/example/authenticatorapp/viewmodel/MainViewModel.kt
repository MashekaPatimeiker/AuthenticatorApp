package com.example.authenticatorapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authenticatorapp.data.network.ApiService
import com.example.authenticatorapp.data.repository.AccountRepository
import com.example.authenticatorapp.domain.models.Account
import com.example.authenticatorapp.domain.models.TotpGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainViewModel : ViewModel() {

    private val apiService = ApiService()
    private val repository = AccountRepository(apiService)

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    init {
        loadAccounts()
        startTimer()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            repository.getAllAccounts().collect { accountList ->
                _accounts.value = accountList
            }
        }
    }

    fun addAccount(account: Account) {
        viewModelScope.launch {
            repository.addAccount(account)
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            repository.deleteAccount(account)
        }
    }

    fun getTotpForAccount(account: Account): TotpGenerator.TotpResult {
        return TotpGenerator.generate(account.secret)
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                _currentTime.value = System.currentTimeMillis()
                delay(1000)
            }
        }
    }
}