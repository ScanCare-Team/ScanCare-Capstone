package com.tariapp.scancare.ui.detail

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.tariapp.scancare.R
import com.tariapp.scancare.databinding.ActivityDetailBinding

class detailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set up View Binding
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val scanName = intent.getStringExtra("scanName") ?: "Nama Scan Tidak Diketahui"
        val status = intent.getStringExtra("status") ?: "Status Tidak Diketahui"
        val imageUri = intent.getStringExtra("imageUri")
        val hazardousMaterials = intent.getStringArrayListExtra("hazardousMaterials")
        val analyses = intent.getStringArrayListExtra("analyses")
        val predictedSkinTypes = intent.getStringArrayListExtra("predictedSkinTypes")

        // Debug logs
        Log.d("HazardousMaterials", hazardousMaterials.toString())
        Log.d("Analyses", analyses.toString())
        Log.d("PredictedSkinTypes", predictedSkinTypes.toString())

        // Bind data to UI
        bindDataToUI(scanName, status, imageUri, hazardousMaterials, analyses, predictedSkinTypes)

    }


    private fun bindDataToUI(
        scanName: String,
        status: String,
        imageUri: String?,
        hazardousMaterials: ArrayList<String>?,
        analyses: ArrayList<String>?,
        predictedSkinTypes: ArrayList<String>?
    ) {
        binding.apply {
            // Set title and status
            tvTitleScan.text = scanName
            statusBahan.text = status

            // Set icon based on status
            val iconResId = if (status == "Bahan Berbahaya Ditemukan") {
                R.drawable.ic_warning // Icon for hazardous materials found
            } else {
                R.drawable.ic_check // Icon for safe
            }
            statusBahan.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)

            // Display image
            if (!imageUri.isNullOrEmpty()) {
                previewImageView.setImageURI(Uri.parse(imageUri))
            }

            // Show data based on status
            if (status == "Bahan Berbahaya Ditemukan") {
                // Show analysis and hazardous materials
                tvAnalisis.visibility = View.VISIBLE
                hasilAnalisis.visibility = View.VISIBLE
                tvHasilAnalisis.visibility = View.VISIBLE
                listBahan.visibility = View.VISIBLE
                tvPredictedSkinTypes.visibility = View.GONE
                listPredictedSkinTypes.visibility = View.GONE

                listBahan.text =
                    hazardousMaterials?.joinToString("\n") { "• $it" } ?: "Bahan tidak ditemukan"
                hasilAnalisis.text =
                    analyses?.joinToString("\n") { "• $it" } ?: "Analisis tidak ditemukan"
            } else {
                // Show predicted skin types
                tvAnalisis.visibility = View.GONE
                hasilAnalisis.visibility = View.GONE
                tvHasilAnalisis.visibility = View.GONE
                listBahan.visibility = View.GONE
                tvPredictedSkinTypes.visibility = View.VISIBLE
                listPredictedSkinTypes.visibility = View.VISIBLE

                listPredictedSkinTypes.text = predictedSkinTypes?.joinToString("\n") { "• $it" }
                    ?: "Jenis kulit tidak tersedia"
            }

            // Back button action
            ivBackButton.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}
