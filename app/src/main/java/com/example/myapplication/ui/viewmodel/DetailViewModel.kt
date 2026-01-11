package com.example.myapplication.ui.viewmodel

import android.util.Log // 1. Import Library Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.EntriHiburan
import com.example.myapplication.data.repository.EntriRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val entri: EntriHiburan) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(private val repository: EntriRepository) : ViewModel() {
    var uiState: DetailUiState by mutableStateOf(DetailUiState.Loading)
        private set

    // Fungsi 1: Mengambil Data Detail (Existing)
    fun getDetailHiburan(id: Int) {
        viewModelScope.launch {
            uiState = DetailUiState.Loading
            try {
                // Mengambil data berdasarkan ID dari Repository
                val entri = repository.getEntriById(id)
                uiState = DetailUiState.Success(entri)
            } catch (e: IOException) {
                uiState = DetailUiState.Error("Jaringan bermasalah. Coba lagi nanti.")
            } catch (e: Exception) {
                uiState = DetailUiState.Error("Gagal memuat data: ${e.message}")
            }
        }
    }

    // Fungsi 2: Menghapus Data Entri dengan Logging
    fun deleteEntri(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // 2. Log Debug: Untuk memastikan ID terkirim
                Log.d("DetailViewModel", "Mencoba menghapus data dengan ID: $id")

                // Memanggil fungsi delete di repository
                repository.deleteEntri(id)

                // Jika tidak ada error (try berhasil), panggil callback sukses
                onSuccess()
            } catch (e: Exception) {
                // 3. Log Error: Untuk melihat pesan error di Logcat
                Log.e("DetailViewModel", "Gagal menghapus: ${e.message}")

                // Panggil callback error dengan pesan
                onError(e.message ?: "Terjadi kesalahan sistem")
            }
        }
    }
}