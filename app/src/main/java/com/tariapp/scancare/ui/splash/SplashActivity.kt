package com.tariapp.scancare.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.tariapp.scancare.MainActivity
import com.tariapp.scancare.R
import com.tariapp.scancare.auth.AuthViewModel
import com.tariapp.scancare.data.ViewModelFactory
import com.tariapp.scancare.ui.login.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(1000)
            val token = viewModel.getToken().first()

            if (token.isNotEmpty()){
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }

    }
}