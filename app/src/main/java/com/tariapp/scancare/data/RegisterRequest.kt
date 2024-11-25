package com.tariapp.scancare.data

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class EditProfileRequest(
    val email: String,
    val fullName: String,
    val oldPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)
