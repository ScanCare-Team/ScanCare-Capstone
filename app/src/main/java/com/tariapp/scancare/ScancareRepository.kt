package com.tariapp.scancare

import android.app.Application
import androidx.lifecycle.LiveData
import com.tariapp.scancare.data.ScancareEntity
import com.tariapp.scancare.data.room.ScanDatabase
import com.tariapp.scancare.data.room.ScancareDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScancareRepository(application: Application){
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val scancareDao: ScancareDao

    init {
        val database = ScanDatabase.getDatabase(application)
        scancareDao = database.scancareDao()

    }

    fun insert(event: ScancareEntity) {
        executorService.execute { scancareDao.insert(event) }
    }

    fun delete(event: ScancareEntity) {
        executorService.execute { scancareDao.delete(event) }
    }

    fun getAllScancare(): LiveData<List<ScancareEntity>> {
        return scancareDao.getAllScancare()
    }

    fun getHistoryById(id: String): LiveData<ScancareEntity> {
        return scancareDao.getScanById(id)
    }

    fun insertScan(scancare: ScancareEntity) {
        executorService.execute { scancareDao.insertScan(scancare) }
    }
}