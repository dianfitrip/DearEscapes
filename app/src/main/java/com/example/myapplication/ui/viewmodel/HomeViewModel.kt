package com.example.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.EntriHiburan
import com.example.myapplication.data.repository.EntriRepository
import com.example.myapplication.data.repository.UserPreferences
import kotlinx.coroutines.flow.SharingStarted // Import untuk stateIn
import kotlinx.coroutines.flow.StateFlow      // Import untuk StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn        // Import untuk stateIn
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val data: List<EntriHiburan>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: EntriRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var uiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    // --- TAMBAHAN MODIFIKASI DIMULAI ---
    // Mengambil Username secara reactive (Live Update)
    // Variabel ini akan dipakai di UI (HalamanHome) untuk menampilkan "Halo, [Nama]"
    val currentUsername: StateFlow<String> = userPreferences.getUsername
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "User" // Default jika loading/kosong
        )
    // --- TAMBAHAN MODIFIKASI BERAKHIR ---

    init {
        getEntries()
    }

    fun getEntries() {
        viewModelScope.launch {
            uiState = HomeUiState.Loading
            try {
                // Ambil User ID yang sedang login saat ini (sekali ambil)
                val currentUserId = userPreferences.getUserId.first()

                val response = repository.getEntri()

                if (response.isSuccessful && response.body()?.success == true) {
                    val allData = response.body()!!.data

                    // Filter Data: Hanya ambil yang userId-nya cocok dengan yang login
                    val filteredData = if (currentUserId != null) {
                        allData.filter { it.userId == currentUserId }
                    } else {
                        // Jika tidak ada user login (error case), kosongkan list
                        emptyList()
                    }

                    uiState = HomeUiState.Success(filteredData)
                } else {
                    uiState = HomeUiState.Error("Gagal: ${response.message()}")
                }
            } catch (e: IOException) {
                uiState = HomeUiState.Error("Masalah Jaringan: Periksa koneksi internet anda")
            } catch (e: Exception) {
                uiState = HomeUiState.Error("Error: ${e.message}")
            }
        }
    }
}