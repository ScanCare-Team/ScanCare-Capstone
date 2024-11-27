package com.tariapp.scancare.ui.scan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tariapp.scancare.R
import com.tariapp.scancare.databinding.ActivityScanCareBinding
import com.tariapp.scancare.getImageUri

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        binding = ActivityScanCareBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                currentImageUri?.let {
                    analyzeImage(it)
                } ?: run {
                    showToast(getString(R.string.empty_image_warning))
                }
            }
        }
    }

    private fun analyzeImage(uri: Uri) {
        binding.progressIndicator.visibility = View.VISIBLE

        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromFilePath(this, uri)

        textRecognizer.process(inputImage)
            .addOnSuccessListener { visionText: Text ->
                val detectedText: String = visionText.text
                if (detectedText.isNotBlank()){
                    binding.progressIndicator.visibility = View.GONE
                    binding.edtDeskripsi.setText(detectedText)
                    binding.edtDeskripsi.visibility = View.VISIBLE
                    binding.btnIdentifyDesc.visibility = View.VISIBLE
                }else{
                    showToast(getString(R.string.no_text_recognized))

                }
            }
            .addOnFailureListener { exception ->
                // Jika proses gagal, sembunyikan loading dan tampilkan pesan error
                binding.progressIndicator.visibility = View.GONE
                showToast(exception.message.toString())
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
        } else {
            currentImageUri = null
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
            showImage()
        } else {
            Toast.makeText(this, "No Media Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
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