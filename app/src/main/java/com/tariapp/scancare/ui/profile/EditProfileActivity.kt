package com.tariapp.scancare.ui.profile

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.tariapp.scancare.R

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val btnBackEdt = findViewById<ImageView>(R.id.btn_back_edt)
        btnBackEdt.setOnClickListener {
            // Selesaikan aktivitas saat ini untuk kembali ke aktivitas sebelumnya
            finish()
        }
    }
}