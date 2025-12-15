package com.example.myapplication.ui.screen.detail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dianfitri.dearescapes.data.model.Entertainment
import com.dianfitri.dearescapes.data.repository.EntertainmentRepository
import com.dianfitri.dearescapes.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: EntertainmentRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Entertainment>>(UiState.Loading)
    val uiState: StateFlow<UiState<Entertainment>> = _uiState

    fun getEntertainmentDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.getEntertainmentDetail(id)
                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Gagal memuat detail")
            }
        }
    }
}