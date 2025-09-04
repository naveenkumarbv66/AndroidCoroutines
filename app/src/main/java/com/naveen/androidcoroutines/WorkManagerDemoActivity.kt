package com.naveen.androidcoroutines

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.naveen.androidcoroutines.ui.theme.AndroidCoroutinesTheme
import com.naveen.androidcoroutines.work.AsyncWorker
import com.naveen.androidcoroutines.work.WithContextWorker
import androidx.compose.ui.platform.LocalContext

class WorkManagerDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidCoroutinesTheme {
                WorkManagerScreen()
            }
        }
    }
}

@Composable
fun WorkManagerScreen() {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    var status by remember { mutableStateOf("Idle") }
    var withContextId by remember { mutableStateOf<String?>(null) }
    var asyncId by remember { mutableStateOf<String?>(null) }
    var withContextParamId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("WorkManager Demo", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Status: $status", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val req = OneTimeWorkRequestBuilder<WithContextWorker>().build()
            withContextId = req.id.toString()
            status = "withContext worker enqueued"
            workManager.enqueue(req)
            workManager.getWorkInfoByIdLiveData(req.id).observeForever { info ->
                if (info != null) {
                    when (info.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            status = info.outputData.getString(WithContextWorker.KEY_RESULT) ?: "withContext worker success"
                        }
                        WorkInfo.State.FAILED -> status = "withContext worker failed"
                        WorkInfo.State.CANCELLED -> status = "withContext worker cancelled"
                        WorkInfo.State.RUNNING -> status = "withContext worker running"
                        else -> {}
                    }
                }
            }
        }) {
            Text("Start withContext Worker")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            withContextId?.let { id ->
                workManager.cancelWorkById(java.util.UUID.fromString(id))
            }
        }) {
            Text("Cancel withContext Worker")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val req = OneTimeWorkRequestBuilder<AsyncWorker>().build()
            asyncId = req.id.toString()
            status = "async worker enqueued"
            workManager.enqueue(req)
            workManager.getWorkInfoByIdLiveData(req.id).observeForever { info ->
                if (info != null) {
                    when (info.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            status = info.outputData.getString(AsyncWorker.KEY_RESULT) ?: "async worker success"
                        }
                        WorkInfo.State.FAILED -> status = "async worker failed"
                        WorkInfo.State.CANCELLED -> status = "async worker cancelled"
                        WorkInfo.State.RUNNING -> status = "async worker running"
                        else -> {}
                    }
                }
            }
        }) {
            Text("Start Async Worker")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            asyncId?.let { id ->
                workManager.cancelWorkById(java.util.UUID.fromString(id))
            }
        }) {
            Text("Cancel Async Worker")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val input = Data.Builder()
                .putInt(WithContextWorker.KEY_REPEAT_COUNT, 5)
                .putLong(WithContextWorker.KEY_DELAY_MS, 400L)
                .putString(WithContextWorker.KEY_MESSAGE, "withContext worker (param)")
                .build()
            val req = OneTimeWorkRequestBuilder<WithContextWorker>()
                .setInputData(input)
                .build()
            withContextParamId = req.id.toString()
            status = "withContext worker (param) enqueued"
            workManager.enqueue(req)
            workManager.getWorkInfoByIdLiveData(req.id).observeForever { info ->
                if (info != null) {
                    when (info.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            status = info.outputData.getString(WithContextWorker.KEY_RESULT) ?: "withContext worker (param) success"
                        }
                        WorkInfo.State.FAILED -> status = "withContext worker (param) failed"
                        WorkInfo.State.CANCELLED -> status = "withContext worker (param) cancelled"
                        WorkInfo.State.RUNNING -> status = "withContext worker (param) running"
                        else -> {}
                    }
                }
            }
        }) {
            Text("Start withContext Worker (Params)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            withContextParamId?.let { id ->
                workManager.cancelWorkById(java.util.UUID.fromString(id))
            }
        }) {
            Text("Cancel withContext Worker (Params)")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkManagerScreenPreview() {
    AndroidCoroutinesTheme {
        WorkManagerScreen()
    }
}


