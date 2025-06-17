package com.campusconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FocusModeViewModel : ViewModel() {

    private val _timeLeft = MutableStateFlow("25:00") // 25 min par défaut
    val timeLeft: StateFlow<String> = _timeLeft

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private var currentTimeSeconds = 25 * 60 // 25 minutes en secondes

    private var timerJob = viewModelScope.launch { }

    fun toggleTimer() {
        if (_isRunning.value) {
            _isRunning.value = false
        } else {
            _isRunning.value = true
            startTimer()
        }
    }

    fun resetTimer() {
        _isRunning.value = false
        currentTimeSeconds = 25 * 60
        _timeLeft.value = formatTime(currentTimeSeconds)
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_isRunning.value && currentTimeSeconds > 0) {
                delay(1000)
                currentTimeSeconds--
                _timeLeft.value = formatTime(currentTimeSeconds)
            }
            if (currentTimeSeconds == 0) _isRunning.value = false
        }
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }
}
