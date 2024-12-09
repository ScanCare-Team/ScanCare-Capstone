package com.tariapp.scancare.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.tariapp.scancare.ScancareRepository
import com.tariapp.scancare.data.ScancareEntity

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    private val scancareRepository: ScancareRepository

    init {
        scancareRepository = ScancareRepository(application)
    }

    fun getHistoryById(id: String): LiveData<ScancareEntity> {
        return scancareRepository.getHistoryById(id)
    }
}