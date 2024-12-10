package com.tariapp.scancare.ui.scan

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.tariapp.scancare.MainViewModel
import com.tariapp.scancare.R
import com.tariapp.scancare.api.ApiConfig
import com.tariapp.scancare.api.response.HazardousMaterialsItem
import com.tariapp.scancare.api.response.PredictRequest
import com.tariapp.scancare.data.ViewModelFactory
import com.tariapp.scancare.databinding.ActivityScanCareBinding
import com.tariapp.scancare.getImageUri
import com.tariapp.scancare.ui.result.ResultActivity
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Suppress("DEPRECATION")
class ScanCareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanCareBinding

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim) }
    private val fromTop: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.from_top_anim) }
    private val toTop: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.to_top_anim) }

    private var clicked = false

    // Variabel untuk menyimpan URI gambar yang dipilih pengguna
    private var currentImageUri: Uri? = null
    private lateinit var viewModel: MainViewModel
    private var previousImageUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanCareBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        enableEdgeToEdge()
        supportActionBar?.hide()

        // Menginisialisasi ViewModel menggunakan ViewModelFactory
        val factory = ViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        // Cek apakah ada URI yang tersimpan di ViewModel
        viewModel.croppedImageUri?.let {
            showImage() // Menampilkan gambar hasil cropping jika ada
            binding.ButtonScan.isEnabled = true
        } ?: viewModel.currentImageUri?.let {
            showImage() // Menampilkan gambar asli jika cropping belum dilakukan
            binding.ButtonScan.isEnabled = true
        } ?: run {
            binding.ButtonScan.isEnabled = false // Nonaktifkan tombol jika tidak ada gambar
        }

        binding.apply {
            fabAdd.setOnClickListener {
                onAddButtonClicked()
            }
            fabCamera.setOnClickListener {
                startCamera()
            }
            fabGalery.setOnClickListener {
                startGallery()
//                Toast.makeText(this@ScanCareActivity, "FAB Galery Clicked", Toast.LENGTH_SHORT).show()
            }
            ivBackButton.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            //klik pada text view untuk mengaktifkan mode edit
            tvTitleScan.setOnClickListener {
                tvTitleScan.visibility = View.GONE
                edtTitleScan.visibility = View.VISIBLE
                edtTitleScan.setText(tvTitleScan.text.toString())
                edtTitleScan.requestFocus()
            }
            //simpan teks saat menekan tombol "Done" pada keyboard
            edtTitleScan.setOnEditorActionListener{_, actionId,_ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveEditedTitle()
                    true
                }else{
                    false
                }
            }
            //simpan teks saat kehilangan fokus dari EditText
            edtTitleScan.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveEditedTitle()
                }
            }
            ButtonScan.setOnClickListener {
                currentImageUri?.let { uri ->
                    uploadImageToOCR(uri)
                } ?: showToast(getString(R.string.empty_image_warning))
            }
            btnIdentifyDesc.setOnClickListener {
                val ocrText = binding.edtDeskripsi.text.toString().trim()
                if (ocrText.isNotBlank()) {
                    analyzeTextForHazard(ocrText)
                } else {
                    showToast("Teks kosong, tidak dapat dianalisis.")
                }
            }
        }
    }

    private suspend fun fetchHazardousMaterials(text: String): List<HazardousMaterialsItem> {
        val apiService = ApiConfig.getPredictApiService()
        val response = apiService.predict(PredictRequest(text))
        Log.d("FetchHazardous", "Response hazardousMaterials: ${response.hazardousMaterials}")
        // Validasi respons untuk memastikan tidak null
        return response.hazardousMaterials ?: emptyList()
    }
    private suspend fun fetchPredictedSkinTypes(ocrText: String): List<String> {
//        val apiService = ApiConfig.getPredictApiService()
//        val response = apiService.predict(PredictRequest(ocrText))
//        return response.predictedSkinTypes
        val apiService = ApiConfig.getPredictApiService()
        val response = apiService.predict(PredictRequest(ocrText))
        Log.d("FetchSkinTypes", "Predicted skin types: ${response.predictedSkinTypes}")
        // Validasi respons agar tidak null atau kosong
        return response.predictedSkinTypes ?: emptyList()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun analyzeTextForHazard(ocrText: String) {
        lifecycleScope.launch {
            binding.progressIndicator.visibility = View.VISIBLE
            try {
                Log.d("AnalyzeText", "Mulai menganalisis teks: $ocrText")

                // Ambil data bahan berbahaya
                val hazardousDetails = fetchHazardousMaterials(ocrText)
                Log.d("AnalyzeText", "Bahan berbahaya ditemukan: $hazardousDetails")

                // Ambil predicted skin types jika tidak ada bahan berbahaya
                val predictedSkinTypes = if (hazardousDetails.isEmpty()) {
                    Log.d("AnalyzeText", "Tidak ada bahan berbahaya. Mulai analisis skin types.")
                    fetchPredictedSkinTypes(ocrText)
                } else {
                    Log.d("AnalyzeText", "Tidak memproses predicted skin types karena bahan berbahaya ditemukan.")
                    null
                }

                // Tampilkan hasil
                showHazardStatus(hazardousDetails, predictedSkinTypes)
            } catch (e: Exception) {
                Log.e("AnalyzeTextError", "Kesalahan saat menganalisis teks: ${e.message}")
                showToast("Gagal menganalisis teks: ${e.message}")
            } finally {
                binding.progressIndicator.visibility = View.GONE
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun showHazardStatus(
        analisisBahan: List<HazardousMaterialsItem>,
        predictedSkinTypes: List<String>?
    ) {
        binding.statusBahan.apply {
            visibility = View.VISIBLE // Tampilkan tombol status

            val status = if (analisisBahan.isNotEmpty())
                "Bahan Berbahaya Ditemukan"
            else
                "Bahan Berbahaya tidak ditemukan"

            Log.d("HazardStatus", "Status: $status") // Logging status untuk debugging

            // Set teks dan ikon berdasarkan hasil analisis
            if (analisisBahan.isNotEmpty()) {
                // Jika bahan berbahaya ditemukan
                text = context.getString(R.string.bahan_berbahaya_ditemukan)
                setTextColor(resources.getColor(R.color.red, theme))
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning, 0, R.drawable.ic_right_arrow, 0)
            } else if (predictedSkinTypes != null && predictedSkinTypes.isNotEmpty()) {
                text = "Prediksi Skin Types: ${predictedSkinTypes.joinToString(", ")}"
                setTextColor(resources.getColor(R.color.ijo_tua, theme))
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, R.drawable.ic_right_arrow, 0)
            } else {
                text = context.getString(R.string.bahan_berbahaya_tidak_ditemukan)
                setTextColor(resources.getColor(R.color.ijo_tua, theme))
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, R.drawable.ic_right_arrow, 0)
            }

            setOnClickListener {
                val intent = Intent(context, ResultActivity::class.java)

                // Kirim gambar analisis
                currentImageUri?.let { uri ->
                    intent.putExtra("imageUri", uri.toString()) // URI dikonversi ke String untuk disimpan dalam intent
                }

                // Kirim status
                intent.putExtra("status", status)
                // Kirim daftar predicted skin types (jika tidak null)
                predictedSkinTypes?.let { skinTypes ->
                    intent.putStringArrayListExtra("predictedSkinTypes", ArrayList(skinTypes))
                }

                intent.putParcelableArrayListExtra("analisisBahan", ArrayList(analisisBahan))
                context.startActivity(intent)
                finish()
            }
        }
    }

    private  fun uploadImageToOCR(imageUri: Uri) {
        lifecycleScope.launch {
            binding.progressIndicator.visibility = View.VISIBLE
            try {
                // Convert URI to File
                val file = getFileFromUri(imageUri)
                    ?: throw IllegalArgumentException("Failed to get file from URI")

                // Prepare multipart request
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // API Call
                val apiService = ApiConfig.getOCRApiService()
                val response = withContext(Dispatchers.IO) {
                    apiService.ocr(imagePart)
                }

                // Handle response
                if (response.isSuccessful) {
                    val ocrResponse = response.body()
                    if (!ocrResponse?.text.isNullOrBlank()) {
                        binding.edtDeskripsi.setText(ocrResponse?.text)
                        binding.edtDeskripsi.visibility = View.VISIBLE
                        binding.btnIdentifyDesc.visibility = View.VISIBLE
                    } else {
                        showToast(getString(R.string.no_text_recognized))
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                showToast("Timeout occurred. Please try again later.")
            } catch (e: Exception) {
                showToast("Error occurred: ${e.message}")
            } finally {
                binding.progressIndicator.visibility = View.GONE
            }
        }
    }
    private fun getFileFromUri(uri: Uri): File? {
        // Convert URI to File
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "temp_image.jpg")
            file.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            file
        } catch (e: Exception) {
            Log.e("FileError", "Error converting URI to file", e)
            null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Menyimpan URI gambar saat konfigurasi berubah (misal: rotasi layar)
        outState.putParcelable("currentImageUri", currentImageUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Mengembalikan URI gambar setelah konfigurasi berubah
        currentImageUri = savedInstanceState.getParcelable("currentImageUri")
        showImage() // Menampilkan gambar kembali
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this) // Buat URI untuk gambar baru
        if (this.currentImageUri != null){
            launcherIntentCamera.launch(currentImageUri!!) //Buka kamera dgn URI
        }else{
            Log.e("AddStoryActivity", "Failed to create URI for camera")
            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    // Hasil pengambilan gambar dari kamera
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
            startCropActivity(uri = currentImageUri!!)
        } else {
            currentImageUri = null
            showToast("Camera capture failed.")
        }
    }

    private fun saveEditedTitle() {
        binding.apply {
            tvTitleScan.text = edtTitleScan.text.toString()
            tvTitleScan.visibility = View.VISIBLE
            edtTitleScan.visibility = View.GONE
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){ uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage() // Update preview dengan gambar baru
            startCropActivity(uri)
        } else {
            currentImageUri?.let {
                showImage() // Tampilkan gambar sebelumnya
                showToast("Selection canceled, showing previous image.")
            } ?: showToast("No image selected and no previous image available.")
        }
    }

    private fun startCropActivity(uri: Uri) {
        previousImageUri = currentImageUri // Simpan URI sebelumnya
        val destinationUri = Uri.fromFile(File(cacheDir, "croppedImage.jpg"))

        val options = UCrop.Options()
        options.setFreeStyleCropEnabled(true) // Memungkinkan cropping bebas
        options.setToolbarTitle("Crop Image") // Judul toolbar

        UCrop.of(uri, destinationUri)
            .withOptions(options)
            .start(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP) {
            when (resultCode) {
                RESULT_OK -> {
                    val croppedUri = UCrop.getOutput(data!!)
                    if (croppedUri != null) {
                        currentImageUri = croppedUri
                        showImage()
                        binding.ButtonScan.isEnabled = true
                    }
                }
                RESULT_CANCELED -> {
                    if (currentImageUri != null) {
                        showImage()
                        Toast.makeText(this, "Cropping canceled, showing previous image.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Cropping canceled and no previous image available.", Toast.LENGTH_SHORT).show()
                    }
                }
                UCrop.RESULT_ERROR -> {
                    val cropError = UCrop.getError(data!!)
                    cropError?.let { Log.e("UCrop Error", it.message.toString()) }
                    Toast.makeText(this, "Cropping failed: ${cropError?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(null) // Reset URI sebelumnya
            binding.previewImageView.setImageURI(it)  // Tetapkan URI gambar baru
            binding.previewImageView.invalidate()     // Paksa ImageView menggambar ulang
        } ?: run {
            Log.e("Image URI", "No image URI available to display")
            binding.previewImageView.setImageDrawable(null) // Hapus gambar jika URI null
        }
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked

    }

    private fun setAnimation(clicked: Boolean) {
         if (!clicked) {
             binding.fabCamera.startAnimation(fromTop)
             binding.fabGalery.startAnimation(fromTop)
             binding.fabAdd.startAnimation(rotateOpen)
         }else{
             binding.fabCamera.startAnimation(toTop)
             binding.fabGalery.startAnimation(toTop)
             binding.fabAdd.startAnimation(rotateClose)
         }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.fabCamera.visibility = View.VISIBLE
            binding.fabGalery.visibility = View.VISIBLE
        }else{
            binding.fabCamera.visibility = View.INVISIBLE
            binding.fabGalery.visibility = View.INVISIBLE
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}