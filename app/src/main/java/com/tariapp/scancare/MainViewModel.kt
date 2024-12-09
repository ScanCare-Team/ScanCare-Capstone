package com.tariapp.scancare

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tariapp.scancare.data.ScancareEntity
import kotlinx.coroutines.launch
import java.util.Stack

class MainViewModel(application: Application): ViewModel()  {
    private val historyRepository: ScancareRepository = ScancareRepository(application)
//    val imageUriStack = Stack<Uri>()  // Stack untuk menyimpan urutan URI gambar
    var croppedImageUri: Uri? = null
    var currentImageUri: Uri? = null
//    var originalImageUri: Uri? = null // URI asli gambar dari galeri
    //untuk menyimpan URI asli dari galeri sebelum proses cropping.

    fun getAllScancare(): LiveData<List<ScancareEntity>> {
        return historyRepository.getAllScancare()
    }
    val historyList: LiveData<List<ScancareEntity>> = historyRepository.getAllScancare()


    fun insert(asclepius: ScancareEntity) {
        viewModelScope.launch {
            historyRepository.insert(asclepius)
        }
    }

    fun delete(asclepius: ScancareEntity) {
        viewModelScope.launch {
            historyRepository.delete(asclepius)
        }
    }

}