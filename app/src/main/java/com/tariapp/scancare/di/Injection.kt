package com.tariapp.scancare.di

import android.app.Application
import android.content.Context
import com.tariapp.scancare.api.ApiConfig
import com.tariapp.scancare.auth.AuthRepository
import com.tariapp.scancare.data.pref.UserPreference
import com.tariapp.scancare.data.pref.dataStore

object Injection {
    fun userPreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService, pref)
    }

    // Fungsi untuk menyediakan instance Application
    fun provideApplication(context: Context): Application {
        return context.applicationContext as Application
    }
}