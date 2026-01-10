
package com.example.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.apiservice.RetrofitClient
import com.example.myapplication.data.model.EntriHiburan
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val data: List<EntriHiburan>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel : ViewModel() {
    var uiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getEntries()
    }

    fun getEntries() {
        viewModelScope.launch {
            uiState = HomeUiState.Loading
            try {
                // Panggil API
                val response = RetrofitClient.instance.getEntertainments()

                // Cek apakah request sukses HTTP (200 OK) DAN success == true dari JSON
                if (response.isSuccessful && response.body()?.success == true) {
                    // Ambil data list dari response body
                    val dataList = response.body()!!.data
                    uiState = HomeUiState.Success(dataList)
                } else {
                    // Jika sukses HTTP tapi success false, atau body null
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