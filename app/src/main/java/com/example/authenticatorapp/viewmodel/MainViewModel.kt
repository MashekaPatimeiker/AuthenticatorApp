package com.example.authenticatorapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authenticatorapp.model.TotpGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainViewModel : ViewModel() {

    // Тестовый секрет (для Google Authenticator используй "JBSWY3DPEHPK3PXP")
    private val testSecret = "JBSWY3DPEHPK3PXP"

    private val _totpState = MutableStateFlow(TotpGenerator.generate(testSecret))
    val totpState: StateFlow<TotpGenerator.TotpResult> = _totpState.asStateFlow()

    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                _totpState.value = TotpGenerator.generate(testSecret)
                delay(1000) // Обновляем каждую секунду
            }
        }
    }
}