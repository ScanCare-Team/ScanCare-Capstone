package com.tariapp.scancare

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Format nama file untuk gambar yang diambil (menggunakan timestamp)
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

// Timestamp saat ini untuk digunakan dalam nama file
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

// Ukuran maksimal file gambar yang diinginkan (1 MB)
private const val MAXIMAL_SIZE = 1000000

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

/**
 * Membuat file sementara untuk menyimpan gambar.
 */
//fun createCustomTempFile(context: Context): File {
//    val filesDir = context.externalCacheDir // Direktori cache eksternal
//    return File.createTempFile(timeStamp, ".jpg", filesDir) // File sementara dengan nama unik
//}
//
///**
// * Mengonversi URI menjadi file lokal untuk manipulasi lebih lanjut.
// */
//fun uriToFile(imageUri: Uri, context: Context): File {
//    val myFile = createCustomTempFile(context) // File sementara untuk menyimpan data
//    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
//    val outputStream = FileOutputStream(myFile)
//    val buffer = ByteArray(1024) // Buffer untuk membaca data
//    var length: Int
//    // Menyalin data dari InputStream ke OutputStream
//    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
//    outputStream.close()
//    inputStream.close()
//    return myFile
//}
//
///**
// * Mengurangi ukuran file gambar agar sesuai dengan ukuran maksimal.
// */
//@RequiresApi(Build.VERSION_CODES.Q)
//fun File.reduceFileImage(): File {
//    val file = this
//    // Decode file menjadi bitmap dan pastikan orientasi benar
//    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
//
//    var compressQuality = 100 // Kualitas awal (maksimal)
//    var streamLength: Int
//
//    // Ulangi proses kompresi hingga ukuran file sesuai batas
//    do {
//        val bmpStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
//        val bmpPictByteArray = bmpStream.toByteArray()
//        streamLength = bmpPictByteArray.size
//        compressQuality -= 5 // Kurangi kualitas secara bertahap
//    } while (streamLength > MAXIMAL_SIZE)
//
//    // Simpan bitmap yang dikompres ke file asli
//    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
//    return file
//}
//
///**
// * Memutar bitmap sesuai orientasi gambar (Exif metadata).
// */
//@RequiresApi(Build.VERSION_CODES.Q)
//fun Bitmap.getRotatedBitmap(file: File): Bitmap {
//    // Membaca orientasi gambar dari metadata Exif
//    val orientation = ExifInterface(file).getAttributeInt(
//        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
//    )
//    // Mengembalikan bitmap dengan rotasi yang sesuai
//    return when (orientation) {
//        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
//        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
//        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
//        ExifInterface.ORIENTATION_NORMAL -> this
//        else -> this
//    }
//}
//
///**
// * Fungsi utilitas untuk memutar gambar.
// */
//fun rotateImage(source: Bitmap, angle: Float): Bitmap {
//    val matrix = Matrix()
//    matrix.postRotate(angle) // Menambahkan rotasi ke matrix
//    return Bitmap.createBitmap(
//        source, 0, 0, source.width, source.height, matrix, true // Membuat bitmap baru dengan rotasi
//    )
//}
