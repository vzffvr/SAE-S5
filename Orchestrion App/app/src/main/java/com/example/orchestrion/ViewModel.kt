package com.example.orchestrion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModel: ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isready = _isReady.asStateFlow()

    var channel = 1
        get() = field
        set(value) {
            field = value
        }


    init {
        viewModelScope.launch {
            delay(1000L)
            _isReady.value = true
        }
    }

}