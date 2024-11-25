package com.tariapp.scancare.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tariapp.scancare.data.pref.UserPreference
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository, private val userPreference: UserPreference) : ViewModel() {

    fun editProfile(email: String, fullName: String, oldPassword: String, newPassword: String, confirmNewPassword: String) =
        authRepository.editProfile(email, fullName, oldPassword, newPassword, confirmNewPassword)

    fun register(name: String, email: String, password: String, confirmPassword: String) =
        authRepository.register(name, email, password, confirmPassword)

    fun login(email: String, password: String) = authRepository.login(email, password)


    fun getToken() = userPreference.getToken()
    // Fungsi untuk logout
    fun logout() {
        viewModelScope.launch {
            userPreference.logout() // Hapus token
        }
    }

}