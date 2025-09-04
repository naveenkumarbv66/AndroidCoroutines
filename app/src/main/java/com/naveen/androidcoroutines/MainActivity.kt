package com.naveen.androidcoroutines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.naveen.androidcoroutines.ui.theme.AndroidCoroutinesTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidCoroutinesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CoroutinesExample(
                        modifier = Modifier.padding(innerPadding),
                        onStartCoroutine = { onCompleted -> startCoroutineExample(onCompleted) },
                        onStartAsync = { onCompleted -> startAsyncExample(onCompleted) },
                        onStartLaunchOnly = { onCompleted -> startLaunchOnlyExample(onCompleted) }
                    )
                }
            }
        }
    }

    private fun startCoroutineExample(onCompleted: (String) -> Unit) {
        // Launch on the Activity's lifecycleScope (runs on Main dispatcher by default)
        lifecycleScope.launch {
            // Switch to a background thread for IO-bound work
            val data: String = withContext(Dispatchers.IO) {
                // Simulate network/database call
                delay(1500)
                "Fetched data on IO thread at ${System.currentTimeMillis()}"
            }

            // Back on Main thread after withContext returns; safe to update UI/state
            onCompleted(data)
        }
    }

    private fun startAsyncExample(onCompleted: (String) -> Unit) {
        // Demonstrates running two background tasks in parallel using async/await
        lifecycleScope.launch {
            val taskA = async(Dispatchers.IO) {
                delay(1000)
                "taskA Completed"
            }
            val taskB = async(Dispatchers.IO) {
                delay(1200)
                "taskB Completed"
            }

            val result = "Parallel results: ${taskA.await()} | ${taskB.await()}"
            onCompleted(result)
        }
    }

    private fun startLaunchOnlyExample(onCompleted: (String) -> Unit) {
        // Plain launch: switch dispatcher at launch and finish on Main for UI update
        lifecycleScope.launch(Dispatchers.Default) {
            delay(700)
            val step1 = "Launch step 1 done"
            delay(600)
            val step2 = "Launch step 2 done"

            withContext(Dispatchers.Main) {
                onCompleted("Launch completed: $step1, $step2")
            }
        }
    }
}

@Composable
fun CoroutinesExample(
    modifier: Modifier = Modifier,
    onStartCoroutine: (onCompleted: (String) -> Unit) -> Unit,
    onStartAsync: (onCompleted: (String) -> Unit) -> Unit,
    onStartLaunchOnly: (onCompleted: (String) -> Unit) -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "LifecycleScope + withContext / async / launch Example",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    result = null
                    isRunning = true
                    onStartCoroutine { data ->
                        result = data
                        isRunning = false
                    }
                },
                enabled = !isRunning
            ) {
                Text(if (isRunning) "Loading..." else "Fetch Data")
            }

            Button(
                onClick = {
                    result = null
                    isRunning = true
                    onStartAsync { data ->
                        result = data
                        isRunning = false
                    }
                },
                enabled = !isRunning
            ) {
                Text(if (isRunning) "Loading..." else "Fetch Parallel")
            }

            Button(
                onClick = {
                    result = null
                    isRunning = true
                    onStartLaunchOnly { data ->
                        result = data
                        isRunning = false
                    }
                },
                enabled = !isRunning
            ) {
                Text(if (isRunning) "Loading..." else "Launch Only")
            }
        }

        if (isRunning) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Working in background...")
        }

        result?.let { value ->
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Result: $value",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "This shows:\n• lifecycleScope.launch on Main + withContext(Dispatchers.IO)\n• async/await for parallel tasks\n• launch(Dispatchers.Default) for fire-and-forget style work",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CoroutinesExamplePreview() {
    AndroidCoroutinesTheme {
        CoroutinesExample(
            onStartCoroutine = { },
            onStartAsync = { },
            onStartLaunchOnly = { }
        )
    }
}