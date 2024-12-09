package com.tariapp.scancare.api

import com.tariapp.scancare.api.response.LoginResponse
import com.tariapp.scancare.api.response.OCRResponse
import com.tariapp.scancare.api.response.PredictRequest
import com.tariapp.scancare.api.response.PredictResponse
import com.tariapp.scancare.api.response.RegisterResponse
import com.tariapp.scancare.data.LoginRequest
import com.tariapp.scancare.data.RegisterRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    @Multipart
    @POST("ocr")
    suspend fun ocr(
        @Part image: MultipartBody.Part
    ): Response<OCRResponse>

    @POST("predict")
    suspend fun predict(
        @Body request: PredictRequest
    ): PredictResponse


}