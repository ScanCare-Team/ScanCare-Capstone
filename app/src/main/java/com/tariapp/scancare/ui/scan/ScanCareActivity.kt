package com.tariapp.scancare.ui.scan

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.tariapp.scancare.R
import com.tariapp.scancare.databinding.ActivityScanCareBinding

class ScanCareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanCareBinding

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim) }
    private val fromTop: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.from_top_anim) }
    private val toTop: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.to_top_anim) }

    private var clicked = false

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
                Toast.makeText(this@ScanCareActivity, "FAB Camera Clicked", Toast.LENGTH_SHORT).show()
            }
            fabGalery.setOnClickListener {
                startGallery()
//                Toast.makeText(this@ScanCareActivity, "FAB Galery Clicked", Toast.LENGTH_SHORT).show()
            }
            ivBackButton.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
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
//            Log.d("Photo Picker", "No media selected")
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
}