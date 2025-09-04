package com.naveen.androidcoroutines

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naveen.androidcoroutines.ui.theme.AndroidCoroutinesTheme
import com.naveen.androidcoroutines.viewmodel.CorotinesDemo
import com.naveen.androidcoroutines.viewmodel.WorkerViewModel

class ViewModelCoroutinesDemo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidCoroutinesTheme {
                val vm: CorotinesDemo = viewModel()
                val workerVm: WorkerViewModel = viewModel()
                ViewModelCoroutinesScreen(vm, workerVm)
            }
        }
    }
}

@Composable
fun ViewModelCoroutinesScreen(vm: CorotinesDemo, workerVm: WorkerViewModel = viewModel()) {
    val status by vm.status.collectAsState()
    var cancellableRunning by remember { mutableStateOf(false) }

    val workInfo = workerVm.workInfo.observeAsState()
    val workerStatus = when (val wi = workInfo.value) {
        null -> "Worker: Idle"
        else -> "Worker: ${wi.state}" + (wi.outputData.getString("result")?.let { ", $it" } ?: "")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("ViewModel + withContext Demo", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Status: $status", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { vm.runWithContextDemo() }) {
            Text("Run withContext() via ViewModel")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { vm.runAsyncDemo() }) {
            Text("Run async() via ViewModel")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = {
            if (cancellableRunning) {
                vm.cancelWithContextCancellable()
                cancellableRunning = false
            } else {
                vm.runWithContextCancellable()
                cancellableRunning = true
            }
        }) {
            Text(if (cancellableRunning) "Cancel withContext() Demo" else "Run withContext() Cancellable")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(workerStatus, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { workerVm.startAsyncWorker() }) {
            Text("Start Async CoroutineWorker")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { workerVm.cancelCurrentWork() }) {
            Text("Cancel CoroutineWorker")
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ViewModelCoroutinesScreenPreview() {
    AndroidCoroutinesTheme {
        ViewModelCoroutinesScreen(vm = CorotinesDemo())
    }
}


