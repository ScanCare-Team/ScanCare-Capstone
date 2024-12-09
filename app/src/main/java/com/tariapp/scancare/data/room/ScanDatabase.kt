package com.tariapp.scancare.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tariapp.scancare.Converters
import com.tariapp.scancare.data.ScancareEntity

@Database(entities = [ScancareEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ScanDatabase: RoomDatabase() {
    abstract fun scancareDao(): ScancareDao

    companion object{
        @Volatile
        private var INSTANCE: ScanDatabase ?= null

        @JvmStatic
        fun getDatabase(context: Context): ScanDatabase{
            return INSTANCE ?: synchronized(this){

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScanDatabase::class.java,
                    "Scancare_db"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


}