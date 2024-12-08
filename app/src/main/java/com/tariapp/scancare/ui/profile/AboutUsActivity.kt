package com.tariapp.scancare.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tariapp.scancare.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menggunakan View Binding untuk menemukan tombol
        binding.btnBackAbt.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
