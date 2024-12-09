package com.tariapp.scancare.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tariapp.scancare.ResultState
import com.tariapp.scancare.api.response.ProfileResponse
import com.tariapp.scancare.auth.AuthRepository
import kotlinx.coroutines.launch

class HomeViewModel (private val repository: AuthRepository) : ViewModel() {

    private val _userProfile = MutableLiveData<ResultState<ProfileResponse>>()
    val userProfile: LiveData<ResultState<ProfileResponse>> get() = _userProfile

    fun fetchUserProfile(email: String) {
        _userProfile.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val profile = repository.getUserProfile(email)
                _userProfile.value = ResultState.Success(profile)
            } catch (e: Exception) {
                _userProfile.value = ResultState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
