package com.naveen.androidcoroutines.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AsyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        val a = async(Dispatchers.IO) {
            delay(700)
            "A"
        }
        val b = async(Dispatchers.IO) {
            delay(900)
            "B"
        }
        val resultText = "Async worker done: ${a.await()} & ${b.await()}"
        val output = Data.Builder()
            .putString(KEY_RESULT, resultText)
            .build()
        Result.success(output)
    }

    companion object {
        const val KEY_RESULT = "result"
    }
}


