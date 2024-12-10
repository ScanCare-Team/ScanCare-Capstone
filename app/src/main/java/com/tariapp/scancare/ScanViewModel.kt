package com.tariapp.scancare

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tariapp.scancare.data.ScancareEntity
import com.tariapp.scancare.data.room.ScanDatabase
import com.tariapp.scancare.data.room.ScancareDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val scancareDao: ScancareDao = ScanDatabase.getDatabase(application).scancareDao()
    private val gson = Gson() // Inisialisasi Gson

    fun saveScanToDatabase(
        imageUri: String,
        status: String,
        scanName: String,
        analyses: List<String>,
        hazardousMaterials: List<String>,
        predictedSkinTypes: List<String>
    ) {
        val scanData = ScancareEntity(
            imageUri = imageUri,
            status = status,
            scanName = scanName,
            analyses = gson.toJson(analyses), // Simpan sebagai JSON
            hazardousMaterials = gson.toJson(hazardousMaterials), // Simpan sebagai JSON
            predictedSkinTypes = gson.toJson(predictedSkinTypes) // Simpan sebagai JSON
        )

        // Gunakan Coroutine untuk menyimpan ke database
        viewModelScope.launch(Dispatchers.IO) {
            scancareDao.insertScan(scanData)
        }
    }
}