package com.tariapp.scancare

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Format nama file untuk gambar yang diambil (menggunakan timestamp)
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

// Timestamp saat ini untuk digunakan dalam nama file
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

// Jika perangkat menggunakan Android Q atau lebih baru
fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        // Menyimpan informasi gambar dalam ContentValues
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg") //Nama File
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg") //Tipe File
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/") //Lokasi File

        }
        // Menyisipkan data ke MediaStore untuk mendapatkan URI
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    // Jika tidak mendukung Android Q, gunakan metode alternatif
    return uri ?: getImageUriForPreQ(context)
}

fun getImageUriForPreQ(context: Context): Uri {
    // Direktori untuk menyimpan gambar
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")

    // Buat direktori jika belum ada
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()

    // Mengembalikan URI menggunakan FileProvider
    return FileProvider.getUriForFile(
        context,
        "fileprovider", // Ganti dengan authority yang sesuai di aplikasi Anda
        imageFile
    )
}
