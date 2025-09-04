package com.naveen.androidcoroutines.data

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.naveen.androidcoroutines.work.AsyncWorker
import java.util.UUID

class WorkRepository(context: Context) {
    private val workManager: WorkManager = WorkManager.getInstance(context.applicationContext)

    fun startAsyncWorker(): UUID {
        val request: OneTimeWorkRequest = OneTimeWorkRequestBuilder<AsyncWorker>().build()
        workManager.enqueue(request)
        return request.id
    }

    fun cancelWork(id: UUID) {
        workManager.cancelWorkById(id)
    }

    fun getWorkInfoLiveData(id: UUID) = workManager.getWorkInfoByIdLiveData(id)

    fun getWorkManager(): WorkManager = workManager
}


