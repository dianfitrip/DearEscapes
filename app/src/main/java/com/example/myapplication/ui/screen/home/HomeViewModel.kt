package com.example.myapplication.ui.screen.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Entertainment
import com.example.myapplication.data.repository.EntertainmentRepository
import com.example.myapplication.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: EntertainmentRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Entertainment>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Entertainment>>> = _uiState

    fun getAllEntertainments() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.getEntertainments()
                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Gagal memuat data")
            }
        }
    }
}