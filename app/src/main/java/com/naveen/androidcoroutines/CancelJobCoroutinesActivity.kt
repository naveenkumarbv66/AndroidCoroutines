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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.naveen.androidcoroutines.ui.theme.AndroidCoroutinesTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewModelScopeActivity : ComponentActivity() {
    private var runningJob: Job? = null
    private var demoJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidCoroutinesTheme {
                HelloContent(
                    onStart = { onTick -> startJob(onTick) },
                    onCancel = { cancelJob() },
                    onStartCancelDemo = { onTick -> startCancelDemo(onTick) }
                )
            }
        }
    }

    private fun startJob(onTick: (String) -> Unit) {
        // Cancel any existing job before starting a new one
        runningJob?.cancel()
        runningJob = lifecycleScope.launch {
            onTick("Started")
            repeat(10) { i ->
                delay(500)
                onTick("Working... step ${i + 1}/10")
            }
            onTick("Completed")
        }
    }

    private fun cancelJob() {
        runningJob?.cancel()
        runningJob = null
    }

    private fun startCancelDemo(onTick: (String) -> Unit) {
        // Cancel any previous demo run
        demoJob?.cancel()
        // Start a job and then cancel it after 2 seconds to demonstrate cancellation
        demoJob = lifecycleScope.launch {
            try {
                onTick("Demo: Started")
                var step = 0
                while (true) {
                    delay(300)
                    step++
                    onTick("Demo: Working... step $step")
                }
            } catch (e: CancellationException) {
                onTick("Demo: Cancelled")
                throw e
            } finally {
                // Optional clean-up could go here
            }
        }
        // Schedule the cancellation
        lifecycleScope.launch {
            delay(2000)
            demoJob?.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ensure we don't leak running coroutines when Activity is finishing
        runningJob?.cancel()
        runningJob = null
        demoJob?.cancel()
        demoJob = null
    }
}

@Composable
fun HelloContent(
    onStart: (onTick: (String) -> Unit) -> Unit,
    onCancel: () -> Unit,
    onStartCancelDemo: (onTick: (String) -> Unit) -> Unit
) {
    var status by remember { mutableStateOf("Idle") }
    var isRunning by remember { mutableStateOf(false) }
    var isDemoRunning by remember { mutableStateOf(false) }

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
                        status = "Cancelled"
                        isRunning = false
                    } else {
                        onStart { tick ->
                            status = tick
                            isRunning = tick != "Completed"
                        }
                        isRunning = true
                    }
                },
                enabled = !isDemoRunning
            ) {
                Text(if (isRunning) "Cancel Job" else "Start Job")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (!isDemoRunning) {
                        isDemoRunning = true
                        status = "Demo: Starting..."
                        onStartCancelDemo { tick ->
                            status = tick
                            if (tick.startsWith("Demo: Cancelled") || tick.startsWith("Demo: Completed")) {
                                isDemoRunning = false
                            }
                        }
                    }
                },
                enabled = !isRunning && !isDemoRunning
            ) {
                Text("Run Cancel Demo")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelloContentPreview() {
    AndroidCoroutinesTheme {
        HelloContent(onStart = { }, onCancel = { }, onStartCancelDemo = { })
    }
}


