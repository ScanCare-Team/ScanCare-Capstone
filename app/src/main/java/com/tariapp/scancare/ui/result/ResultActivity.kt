@file:Suppress("DEPRECATION", "NAME_SHADOWING")

package com.tariapp.scancare.ui.result

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tariapp.scancare.R
import com.tariapp.scancare.ScanViewModel
import com.tariapp.scancare.api.response.HazardousMaterialsItem
import com.tariapp.scancare.databinding.ActivityResultBinding
import java.io.File
import java.io.FileOutputStream

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var scanViewModel: ScanViewModel
    private var currentTitleName: String = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        // Initialize View Binding
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        scanViewModel = ViewModelProvider(this).get(ScanViewModel::class.java)

        setupUI()
        populateData()
    }

    /**
     * Setup UI listeners and interactions
     */
    private fun setupUI() {
        binding.apply {
            ivBackButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            // Activate edit mode when clicking on the title
            tvTitleScan.setOnClickListener {
                enableEditMode()
            }

            // Save title on "Done" action from the keyboard
            edtTitleScan.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveEditedTitle()
                    true
                } else {
                    false
                }
            }

            // Save title and update the database when losing focus
            edtTitleScan.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && currentTitleName != edtTitleScan.text.toString()) {
                    saveEditedTitle()
                    saveScanToDatabase()
                }
            }
        }
    }

    /**
     * Populate the UI with data received from the intent
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun populateData() {
        val imageUri = intent.getStringExtra("imageUri") ?: ""
        val status = intent.getStringExtra("status") ?: ""
        val scanName = intent.getStringExtra("scanName") ?: "Nama Scan"
        val hazardousDetails =
            intent.getParcelableArrayListExtra<HazardousMaterialsItem>("analisisBahan")?: emptyList()
//        val hazardousMaterials =
//            intent.getParcelableArrayListExtra<HazardousMaterialsItem>("hazardousMaterials") ?: emptyList()
        val predictedSkinTypes =
            intent.getStringArrayListExtra("predictedSkinTypes")

//        val hazardousDetailsJson = Gson().toJson(hazardousDetails)
//        val hazardousMaterialsJson = Gson().toJson(hazardousMaterials)

        // Set initial scan name
        currentTitleName = scanName
        binding.tvTitleScan.text = currentTitleName

        // Display image
        binding.previewImageView.setImageURI(Uri.parse(imageUri))

        // Display hazardous status
        displayStatus(status)

        // Populate hazardous materials and skin types
        if (hazardousDetails.isNullOrEmpty()) {
            displayNoHazardousMaterials(predictedSkinTypes)
        } else {
            displayHazardousMaterials(hazardousDetails)
        }
    }

    /**
     * Update hazardous status and icon based on the status
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun displayStatus(status: String) {
        binding.apply {
            statusBahan.text = status
            if (status == "Bahan Berbahaya Ditemukan") {
                statusBahan.setTextColor(getColor(R.color.red))
                statusBahan.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning, 0, 0, 0)
            } else {
                statusBahan.setTextColor(getColor(R.color.ijo_tua))
                statusBahan.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0)
            }
        }
    }

    /**
     * Display predicted skin types and hide hazardous materials section if none are found
     */
    private fun displayNoHazardousMaterials(predictedSkinTypes: List<String>?) {
        binding.apply {
            progressBar.visibility = View.GONE
            tvAnalisis.visibility = View.INVISIBLE
            hasilAnalisis.visibility = View.INVISIBLE
            tvHasilAnalisis.visibility = View.INVISIBLE
            listBahan.visibility = View.INVISIBLE

            predictedSkinTypes?.let { skinTypes ->
                if (skinTypes.isNotEmpty()) {
                    listPredictedSkinTypes.text = skinTypes.joinToString("\n") { "• $it" }
                    listPredictedSkinTypes.visibility = View.VISIBLE
                } else {
                    listPredictedSkinTypes.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Display hazardous materials and analysis results
     */
    private fun displayHazardousMaterials(
        hazardousDetails: List<HazardousMaterialsItem>
    ) {
//        val hazardousDetails: List<HazardousMaterialsItem> = try {
//            Gson().fromJson(hazardousDetailsJson, object : TypeToken<List<HazardousMaterialsItem>>() {}.type)
//        } catch (e: Exception) {
//            emptyList()
//        }
//
//        val hazardousMaterials: List<HazardousMaterialsItem> = try {
//            Gson().fromJson(hazardousMaterialsJson, object : TypeToken<List<HazardousMaterialsItem>>() {}.type)
//        } catch (e: Exception) {
//            emptyList()
//        }
        binding.apply {
            progressBar.visibility = View.GONE
            tvAnalisis.visibility = View.VISIBLE
            hasilAnalisis.visibility = View.VISIBLE
            tvHasilAnalisis.visibility = View.VISIBLE
            listBahan.visibility = View.VISIBLE

            hasilAnalisis.text = hazardousDetails.joinToString("\n") { "• ${it.analisis}" }
            listBahan.text = hazardousDetails
                .mapNotNull { it.bahanBerbahaya.takeIf { bahan -> bahan.isNotBlank() } }
                .joinToString("\n") { "• $it" }

            listPredictedSkinTypes.visibility = View.GONE
            tvPredictedSkinTypes.visibility = View.GONE
        }
    }

    /**
     * Enable title edit mode
     */
    private fun enableEditMode() {
        binding.apply {
            tvTitleScan.visibility = View.GONE
            edtTitleScan.visibility = View.VISIBLE
            edtTitleScan.setText(tvTitleScan.text.toString())
            edtTitleScan.requestFocus()
        }
    }

    /**
     * Save the edited title
     */
    private fun saveEditedTitle() {
        binding.apply {
            val newTitle = edtTitleScan.text.toString()
            tvTitleScan.text = newTitle
            tvTitleScan.visibility = View.VISIBLE
            edtTitleScan.visibility = View.GONE
            currentTitleName = newTitle
        }
    }

    /**
     * Save the scan to the database
     */
    private fun saveScanToDatabase() {
        val drawable = binding.previewImageView.drawable
        if (drawable == null) {
            throw IllegalStateException("Drawable is null. Cannot convert to Bitmap.")
        }

        val bitmap = drawable.toBitmap()
        val imageUri = generateUniqueUri(this, bitmap)

        val hazardousDetails =
            intent.getParcelableArrayListExtra<HazardousMaterialsItem>("analisisBahan") ?: emptyList()
        val predictedSkinTypes =
            intent.getStringArrayListExtra("predictedSkinTypes") ?: emptyList()

        // Pisahkan analisis bahan dan bahan berbahaya
        val analyses = hazardousDetails.mapNotNull { it.analisis.takeIf { it.isNotBlank() } }
        val hazardousMaterials = hazardousDetails.mapNotNull { it.bahanBerbahaya.takeIf { it.isNotBlank() } }


        scanViewModel.saveScanToDatabase(
            imageUri = imageUri.toString(),
            status = intent.getStringExtra("status") ?: "",
            scanName = currentTitleName,
            analyses = analyses,
            hazardousMaterials = hazardousMaterials,
            predictedSkinTypes = predictedSkinTypes
//            imageUri = imageUri.toString(),
//            status = intent.getStringExtra("status") ?: "",
////            hazardousMaterials = intent.getParcelableArrayListExtra<HazardousMaterialsItem>("hazardousMaterials") ?: emptyList(),
//            hazardousMaterials = hazardousMaterialsJson,
//            predictedSkinTypes = intent.getStringArrayListExtra("predictedSkinTypes") ?: emptyList(),
////            hazardousDetails = intent.getParcelableArrayListExtra<HazardousMaterialsItem>("hazardousDetails") ?: emptyList(),
//            hazardousDetails = hazardousDetailsJson,
//            scanName = currentTitleName
        )
    }

    /**
     * Generate a unique URI for the image
     */
    private fun generateUniqueUri(context: Context, image: Bitmap): Uri {
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)

        FileOutputStream(file).use { outputStream ->
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        // Gunakan `FileProvider` untuk menghasilkan URI yang kompatibel dengan Android
        return Uri.fromFile(file)
    }
}
