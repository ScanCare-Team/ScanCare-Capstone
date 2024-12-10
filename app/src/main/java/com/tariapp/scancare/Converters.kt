package com.tariapp.scancare

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.tariapp.scancare.api.response.HazardousMaterialsItem

class Converters {
    private val gson = Gson()

    // Mengonversi List<HazardousMaterialsItem> menjadi JSON String
    @TypeConverter
    fun fromHazardousMaterialsList(value: List<HazardousMaterialsItem>?): String? {
        return try {
            gson.toJson(value) // Mengonversi List menjadi JSON String
        } catch (e: Exception) {
            Log.e("JsonError", "Error serializing List<HazardousMaterialsItem>: ${e.message}")
            null  // Mengembalikan null jika terjadi error
        }
    }

    // Mengonversi JSON String menjadi List<HazardousMaterialsItem>
    @TypeConverter
    fun toHazardousMaterialsList(value: String?): List<HazardousMaterialsItem>? {
        return try {
            if (value.isNullOrEmpty()) return null
            val type = object : TypeToken<List<HazardousMaterialsItem>>() {}.type
            gson.fromJson(value, type)  // Mengonversi JSON String menjadi List<HazardousMaterialsItem>
        } catch (e: JsonSyntaxException) {
            Log.e("JsonError", "Error deserializing List<HazardousMaterialsItem>: ${e.message}")
            null  // Mengembalikan null jika terjadi error
        }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return try {
            if (value.isNullOrEmpty()) return null
            gson.toJson(value)  // Mengubah List menjadi JSON String
        } catch (e: Exception) {
            Log.e("JsonError", "Error serializing List<String>: ${e.message}")
            null  // Mengembalikan null jika terjadi error
        }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return try {
            if (value.isNullOrEmpty()) return emptyList()  // Mengembalikan List kosong jika null atau kosong
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(value, type)  // Mengubah JSON String menjadi List<String>
        } catch (e: JsonSyntaxException) {
            Log.e("JsonError", "Error deserializing List<String>: ${e.message}")
            emptyList()  // Mengembalikan List kosong jika terjadi error
        }
    }
}
