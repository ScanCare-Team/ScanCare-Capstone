package com.tariapp.scancare.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tariapp.scancare.ResultState
import com.tariapp.scancare.auth.AuthViewModel
import com.tariapp.scancare.data.ViewModelFactory
import com.tariapp.scancare.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi View Binding
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackEdt.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        // Inisialisasi ViewModel menggunakan ViewModelFactory
        val factory = ViewModelFactory.getInstance(applicationContext)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Setup tombol Save Changes
        binding.btnUpdateProfile.setOnClickListener {
            val fullName = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val oldPassword = binding.edtOldPw.text.toString()
            val newPassword = binding.edtNewPw.text.toString()
            val confirmNewPassword = binding.cfrPw.text.toString()

            // Validasi Input
            if (fullName.isEmpty() || email.isEmpty() || oldPassword.isEmpty() ||
                newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmNewPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil fungsi edit profile di ViewModel
            observeEditProfile(email, fullName, oldPassword, newPassword, confirmNewPassword)
        }
    }

    private fun observeEditProfile(
        email: String,
        fullName: String,
        oldPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ) {
        authViewModel.editProfile(email, fullName, oldPassword, newPassword, confirmNewPassword)
            .observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {

                    }
                    is ResultState.Success -> {

                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is ResultState.Error -> {

                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
