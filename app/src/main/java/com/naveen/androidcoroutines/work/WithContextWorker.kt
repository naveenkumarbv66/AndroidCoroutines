package com.naveen.androidcoroutines.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class WithContextWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val repeatCount = inputData.getInt(KEY_REPEAT_COUNT, 10)
        val delayMs = inputData.getLong(KEY_DELAY_MS, 600L)
        val message = inputData.getString(KEY_MESSAGE) ?: "withContext worker"

        var last = ""
        repeat(repeatCount) { i ->
            if (isStopped) return@withContext Result.failure()
            delay(delayMs)
            last = "$message: chunk ${i + 1}/$repeatCount"
        }
        val output = Data.Builder()
            .putString(KEY_RESULT, "$message done: $last")
            .build()
        Result.success(output)
    }

    companion object {
        const val KEY_RESULT = "result"
        const val KEY_REPEAT_COUNT = "repeatCount"
        const val KEY_DELAY_MS = "delayMs"
        const val KEY_MESSAGE = "message"
    }
}


