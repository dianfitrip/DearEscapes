package com.example.myapplication.ui.screen.statistic


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Statistic
import com.example.myapplication.data.repository.StatisticRepository
import com.example.myapplication.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticViewModel(private val repository: StatisticRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Statistic>>(UiState.Loading)
    val uiState: StateFlow<UiState<Statistic>> = _uiState

    fun getStatistics() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.getStatistics()
                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Gagal memuat statistik")
            }
        }
    }
}