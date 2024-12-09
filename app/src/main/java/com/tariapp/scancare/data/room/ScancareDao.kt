package com.tariapp.scancare.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tariapp.scancare.data.ScancareEntity

@Dao
interface ScancareDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(scancare: ScancareEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScan(scancare: ScancareEntity)

    @Delete
    fun delete(scancare: ScancareEntity)

    @Query("SELECT * from scancare ORDER BY id DESC")
    fun getAllScancare(): LiveData<List<ScancareEntity>>

    @Query("SELECT * FROM scancare WHERE id = :id")
    fun getScanById(id: String): LiveData<ScancareEntity>
}