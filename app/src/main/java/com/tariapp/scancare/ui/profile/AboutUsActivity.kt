package com.tariapp.scancare.ui.profile

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.tariapp.scancare.R

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val btnBackEdt = findViewById<ImageView>(R.id.btn_back_abt)
        btnBackEdt.setOnClickListener {
            // Selesaikan aktivitas saat ini untuk kembali ke aktivitas sebelumnya
            finish()
        }
    }
}