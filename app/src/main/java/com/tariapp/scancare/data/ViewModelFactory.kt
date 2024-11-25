package com.tariapp.scancare.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tariapp.scancare.auth.AuthRepository
import com.tariapp.scancare.auth.AuthViewModel
import com.tariapp.scancare.data.pref.UserPreference
import com.tariapp.scancare.di.Injection
import com.tariapp.scancare.ui.home.HomeViewModel
import com.tariapp.scancare.ui.profile.ProfileViewModel

class ViewModelFactory(
    private val userPreference: UserPreference,
    private val authRepository: AuthRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository, userPreference) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(authRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this){
                instance ?: ViewModelFactory(
                    Injection.userPreference(context),
                    Injection.provideAuthRepository(context),
                )
            }.also { instance = it }
    }
}