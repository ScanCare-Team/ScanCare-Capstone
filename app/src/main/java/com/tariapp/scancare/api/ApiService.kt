package com.tariapp.scancare.api

import com.tariapp.scancare.api.response.LoginResponse
import com.tariapp.scancare.api.response.EditProfileResponse
import com.tariapp.scancare.api.response.ProfileResponse
import com.tariapp.scancare.api.response.RegisterResponse
import com.tariapp.scancare.data.EditProfileRequest
import com.tariapp.scancare.data.LoginRequest
import com.tariapp.scancare.data.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @Headers ("Content-Type: application/json")
    @PUT("api/auth/user/profile")
    suspend fun editProfile(
       @Body request: EditProfileRequest
    ): EditProfileResponse

    @Headers ("Content-Type: application/json")
    @GET("api/auth/user/{email}")
    suspend fun getUserProfile(
        @Path("email") email: String
    ): ProfileResponse
}