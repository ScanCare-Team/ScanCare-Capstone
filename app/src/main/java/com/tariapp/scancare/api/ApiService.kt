package com.tariapp.scancare.api

import com.tariapp.scancare.api.response.LoginResponse
import com.tariapp.scancare.api.response.RegisterResponse
import com.tariapp.scancare.data.LoginRequest
import com.tariapp.scancare.data.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @Headers("Content-Type: application/json")
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}