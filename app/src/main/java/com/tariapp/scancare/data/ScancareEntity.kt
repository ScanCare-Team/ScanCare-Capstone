package com.tariapp.scancare.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "scancare")
@Parcelize
data class ScancareEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID unik untuk setiap pemindaian
    val imageUri: String,
    val status: String,
    val scanName: String,
    val analyses: String, // Simpan sebagai JSON
    val hazardousMaterials: String, // Simpan sebagai JSON
    val predictedSkinTypes: String // Simpan sebagai JSON
//    @TypeConverters(Converters::class) val hazardousMaterials: List<HazardousMaterialsItem>, // List bahan berbahaya jika ada
//    @TypeConverters(Converters::class) val hazardousDetails: List<HazardousMaterialsItem>, // Detail analisis bahan berbahaya jika ada
//    @TypeConverters(Converters::class) val predictedSkinTypes: List<String> // List jenis kulit yang diprediksi
):Parcelable
