package com.example.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.apiservice.RetrofitClient
import com.example.myapplication.data.model.StatistikDataModel
import com.example.myapplication.data.repository.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


sealed interface StatistikUiState {
    object Loading : StatistikUiState
    data class Success(val data: StatistikDataModel) : StatistikUiState
    data class Error(val message: String) : StatistikUiState
}

class StatistikViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    var uiState: StatistikUiState by mutableStateOf(StatistikUiState.Loading)
        private set

    fun fetchStatistik() {
        viewModelScope.launch {
            uiState = StatistikUiState.Loading
            try {
                val currentUserId = userPreferences.getUserId.first()
                if (currentUserId == null) {
                    uiState = StatistikUiState.Error("User tidak ditemukan")
                    return@launch
                }

                val response = RetrofitClient.instance.getUserStatistics(currentUserId)

                if (response.isSuccessful && response.body()?.success == true) {
                    val statsData = response.body()!!.data
                    uiState = StatistikUiState.Success(statsData)
                } else {
                    uiState = StatistikUiState.Error("Gagal memuat statistik")
                }
            } catch (e: Exception) {
                uiState = StatistikUiState.Error("Error: ${e.localizedMessage}")
            }
        }
    }
}