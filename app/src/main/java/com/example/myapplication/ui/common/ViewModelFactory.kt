package com.example.myapplication.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.api.ApiConfig
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.EntertainmentRepository
import com.example.myapplication.data.repository.StatisticRepository
import com.example.myapplication.ui.screen.add.AddViewModel
import com.example.myapplication.ui.screen.auth.AuthViewModel
import com.example.myapplication.ui.screen.detail.DetailViewModel
import com.example.myapplication.ui.screen.edit.EditViewModel
import com.example.myapplication.ui.screen.home.HomeViewModel
import com.example.myapplication.ui.screen.statistic.StatisticViewModel

class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiService = ApiConfig.getApiService()

        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val repository = AuthRepository.getInstance(apiService)
            return AuthViewModel(repository) as T
        }
        else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val repository = EntertainmentRepository.getInstance(apiService)
            return HomeViewModel(repository) as T
        }
        else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            val repository = EntertainmentRepository.getInstance(apiService)
            return DetailViewModel(repository) as T
        }
        else if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            val repository = EntertainmentRepository.getInstance(apiService)
            return AddViewModel(repository) as T
        }
        else if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            val repository = EntertainmentRepository.getInstance(apiService)
            return EditViewModel(repository) as T
        }
        else if (modelClass.isAssignableFrom(StatisticViewModel::class.java)) {
            val repository = StatisticRepository.getInstance(apiService)
            return StatisticViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}