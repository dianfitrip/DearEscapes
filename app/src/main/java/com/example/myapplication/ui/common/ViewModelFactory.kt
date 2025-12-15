package com.example.myapplication.ui.common


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.api.ApiConfig
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.ui.screen.auth.AuthViewModel

class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Setup manual dependency
            val apiService = ApiConfig.getApiService()
            val repository = AuthRepository.getInstance(apiService)
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}