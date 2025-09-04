package com.naveen.androidcoroutines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.naveen.androidcoroutines.ui.theme.AndroidCoroutinesTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AsynCoroutinesDemo : ComponentActivity() {
    private var parentJob: Job? = null
    private var withContextJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidCoroutinesTheme {
                AsyncHelloScreen(
                    onStart = { onStatus -> startAsyncOperation(onStatus) },
                    onCancel = { cancelAsyncOperation() },
                    onStartWithContext = { onStatus -> startWithContextOperation(onStatus) },
                    onCancelWithContext = { cancelWithContextOperation() }
                )
            }
        }
    }

    private fun startAsyncOperation(onStatus: (String) -> Unit) {
        parentJob?.cancel()
        parentJob = lifecycleScope.launch {
            try {
                onStatus("Async: Started")

                val taskA = async(Dispatchers.IO) {
                    delay(800)
                    "TaskA Completed"
                }
                val taskB = async(Dispatchers.IO) {
                    delay(1200)
                    "TaskB Completed"
                }

                onStatus("Async: Awaiting results...")
                val result = "${taskA.await()} | ${taskB.await()}"
                onStatus("Async: Done -> $result")
            } catch (e: CancellationException) {
                onStatus("Async: Cancelled")
                throw e
            }
        }
    }

    private fun cancelAsyncOperation() {
        parentJob?.cancel()
        parentJob = null
    }

    private fun startWithContextOperation(onStatus: (String) -> Unit) {
        withContextJob?.cancel()
        withContextJob = lifecycleScope.launch {
            try {
                onStatus("withContext: Started")
                val result = withContext(Dispatchers.IO) {
                    var last = ""
                    repeat(10) { i ->
                        // Simulate chunked IO work
                        delay(500)
                        last = "withContext: Working... chunk ${i + 1}/10"
                        // It's okay to post status occasionally from background for demo purposes
                        onStatus(last)
                    }
                    "withContext: Result ready"
                }
                onStatus("withContext: Done -> $result")
            } catch (e: CancellationException) {
                onStatus("withContext: Cancelled")
                throw e
            }
        }
    }

    private fun cancelWithContextOperation() {
        withContextJob?.cancel()
        withContextJob = null
    }

    override fun onDestroy() {
        super.onDestroy()
        parentJob?.cancel()
        parentJob = null
        withContextJob?.cancel()
        withContextJob = null
    }
}

@Composable
fun AsyncHelloScreen(
    onStart: (onStatus: (String) -> Unit) -> Unit,
    onCancel: () -> Unit,
    onStartWithContext: (onStatus: (String) -> Unit) -> Unit,
    onCancelWithContext: () -> Unit
) {
    var status by remember { mutableStateOf("Idle") }
    var isRunning by remember { mutableStateOf(false) }
    var isRunningWithContext by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Hello", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Status: $status", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (isRunning) {
                        onCancel()
                        status = "Async: Cancelled"
                        isRunning = false
                    } else {
                        onStart { s ->
                            status = s
                            isRunning = !s.startsWith("Async: Done") && !s.startsWith("Async: Cancelled")
                        }
                        isRunning = true
                    }
                }
            ) {
                Text(if (isRunning) "Cancel Async" else "Start Async")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (isRunningWithContext) {
                        onCancelWithContext()
                        status = "withContext: Cancelled"
                        isRunningWithContext = false
                    } else {
                        onStartWithContext { s ->
                            status = s
                            isRunningWithContext = !s.startsWith("withContext: Done") && !s.startsWith("withContext: Cancelled")
                        }
                        isRunningWithContext = true
                    }
                }
            ) {
                Text(if (isRunningWithContext) "Cancel withContext" else "Start withContext")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AsyncHelloScreenPreview() {
    AndroidCoroutinesTheme {
        AsyncHelloScreen(onStart = { }, onCancel = { }, onStartWithContext = { }, onCancelWithContext = { })
    }
}


