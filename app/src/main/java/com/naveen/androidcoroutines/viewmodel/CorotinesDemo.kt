package com.naveen.androidcoroutines.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

class CorotinesDemo : ViewModel() {
    private val _status: MutableStateFlow<String> = MutableStateFlow("Idle")
    val status: StateFlow<String> = _status
    private var withContextJob: Job? = null

    fun runWithContextDemo() {
        viewModelScope.launch {
            _status.value = "Started"
            val result = withContext(Dispatchers.IO) {
                var last = ""
                repeat(5) { i ->
                    delay(400)
                    last = "Working... chunk ${i + 1}/5"
                    // Post intermediate progress safely via StateFlow on the main thread
                    _status.value = last
                }
                "Done -> $last"
            }
            _status.value = result
        }
    }

    fun runAsyncDemo() {
        viewModelScope.launch {
            _status.value = "Async: Started"
            val taskA = async(Dispatchers.IO) {
                delay(1600)
                "TaskA"
            }
            val taskB = async(Dispatchers.IO) {
                delay(1800)
                "TaskB"
            }
            _status.value = "Async: Awaiting results..."
            val result = "${taskA.await()} + ${taskB.await()}"
            _status.value = "Async: Done -> $result"
        }
    }

    fun runWithContextCancellable() {
        withContextJob?.cancel()
        withContextJob = viewModelScope.launch {
            try {
                _status.value = "Cancellable: Started"
                val result = withContext(Dispatchers.IO) {
                    var last = ""
                    repeat(10) { i ->
                        delay(300)
                        last = "Cancellable: Working... step ${i + 1}/10"
                        _status.value = last
                    }
                    "Cancellable: Done -> $last"
                }
                _status.value = result
            } catch (e: CancellationException) {
                _status.value = "Cancellable: Cancelled"
                throw e
            } finally {
                withContextJob = null
            }
        }
    }

    fun cancelWithContextCancellable() {
        withContextJob?.cancel()
        withContextJob = null
    }
}


