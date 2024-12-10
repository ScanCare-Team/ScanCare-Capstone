package com.tariapp.scancare

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tariapp.scancare.data.ScancareEntity
import kotlinx.coroutines.launch

class MainViewModel(application: Application): ViewModel()  {
    private val historyRepository: ScancareRepository = ScancareRepository(application)
    var croppedImageUri: Uri? = null
    var currentImageUri: Uri? = null

    fun getAllScancare(): LiveData<List<ScancareEntity>> {
        return historyRepository.getAllScancare()
    }

    fun delete(asclepius: ScancareEntity) {
        viewModelScope.launch {
            historyRepository.delete(asclepius)
        }
    }

}