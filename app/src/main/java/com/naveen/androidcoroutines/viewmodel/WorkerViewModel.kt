package com.naveen.androidcoroutines.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.naveen.androidcoroutines.data.WorkRepository
import kotlinx.coroutines.launch
import java.util.UUID

class WorkerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WorkRepository(application)

    private val _currentWorkId = MutableLiveData<UUID?>(null)
    val currentWorkId: LiveData<UUID?> = _currentWorkId

    private var currentSource: LiveData<WorkInfo?>? = null
    val workInfo = MediatorLiveData<WorkInfo?>().apply {
        addSource(_currentWorkId) { id ->
            currentSource?.let { removeSource(it) }
            if (id == null) {
                value = null
                currentSource = null
            } else {
                val src = repository.getWorkInfoLiveData(id)
                currentSource = src
                addSource(src) { info -> value = info }
            }
        }
    }

    fun startAsyncWorker() {
        viewModelScope.launch {
            val id = repository.startAsyncWorker()
            _currentWorkId.value = id
        }
    }

    fun cancelCurrentWork() {
        val id = _currentWorkId.value ?: return
        repository.cancelWork(id)
    }
}


