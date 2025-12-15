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
    else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
        val apiService = ApiConfig.getApiService()
        val repository = EntertainmentRepository.getInstance(apiService)
        return DetailViewModel(repository) as T
    }
    else if (modelClass.isAssignableFrom(com.example.myapplication.ui.screen.edit.EditViewModel::class.java)) {
        val apiService = com.example.myapplication.data.api.ApiConfig.getApiService()
        val repository = com.example.myapplication.data.repository.EntertainmentRepository.getInstance(apiService)
        return com.example.myapplication.ui.screen.edit.EditViewModel(repository) as T
    }
    else if (modelClass.isAssignableFrom(StatisticViewModel::class.java)) {
        val apiService = com.example.myapplication.data.api.ApiConfig.getApiService()
        val repository = StatisticRepository.getInstance(apiService)
        return StatisticViewModel(repository) as T
    }
}