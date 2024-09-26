package com.example.orchestrion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashScreenViewModel: ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isready = _isReady.asStateFlow()


    init {
        viewModelScope.launch {
            delay(5000L)
            _isReady.value = true
        }
    }
}