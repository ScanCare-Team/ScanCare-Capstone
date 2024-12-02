package com.tariapp.scancare.auth

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.tariapp.scancare.ResultState
import com.tariapp.scancare.api.ApiService
import com.tariapp.scancare.api.response.LoginResponse
import com.tariapp.scancare.api.response.RegisterResponse
import com.tariapp.scancare.data.LoginRequest
import com.tariapp.scancare.data.RegisterRequest
import com.tariapp.scancare.data.pref.UserModel
import com.tariapp.scancare.data.pref.UserPreference
import retrofit2.HttpException
import java.io.IOException

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
){
    fun register (name: String, email: String, password: String, confirmPassword: String) = liveData {
        emit(ResultState.Loading)
        try {
            val request = RegisterRequest(name, email, password, confirmPassword)
            val response = apiService.register(request)
            emit(ResultState.Success(response))
        }catch (e: HttpException){
            val errorMessage = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorMessage, RegisterResponse::class.java)
            emit(ResultState.Error(errorResponse.message ?: "Terjadi Kesalahan"))
        }catch (e: IOException) { // Tangkap kesalahan jaringan
            emit(ResultState.Error("Network error. Please check your connection."))
        }catch (e: Exception) { // Tangkap kesalahan lainnya
            emit(ResultState.Error("An unexpected error occurred: ${e.message}"))
        }
    }

    fun login(email: String, password: String)= liveData {
        emit(ResultState.Loading)
        try {
            // Bungkus parameter dalam objek LoginRequest
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            val userModel = UserModel(
                name = response.data.name,
                token = response.data.token,
                email = email,
                password = password
            )
            userPreference.saveToken(userModel)
            emit(ResultState.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    suspend fun logout() {
        // Menghapus data pengguna (misalnya, token) dari DataStore
        userPreference.logout() // Pastikan fungsi logout() di UserPreference bekerja dengan benar
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(apiService: ApiService, pref: UserPreference) =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, pref )
            }.also { instance = it }
    }
}