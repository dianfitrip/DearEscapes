package com.example.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.apiservice.RetrofitClient
import com.example.myapplication.data.model.EntriHiburan
import kotlinx.coroutines.launch

class EntryViewModel : ViewModel() {
    var uiState by mutableStateOf(EntryUiState())
        private set

    // Fungsi untuk mengubah isi form saat user mengetik
    fun updateUiState(newDetail: DetailEntri) {
        uiState = EntryUiState(detailEntri = newDetail, isEntryValid = validasiInput(newDetail))
    }

    private fun validasiInput(uiState: DetailEntri = this.uiState.detailEntri): Boolean {
        return uiState.title.isNotBlank() && uiState.description.isNotBlank() && uiState.genre.isNotBlank()
    }

    fun saveEntry(navigateBack: () -> Unit) {
        viewModelScope.launch {
            try {
                // Konversi data dari form (DetailEntri) ke Model Database (EntriHiburan)
                val entriBaru = uiState.detailEntri.toEntriHiburan()

                // Panggil API
                val response = RetrofitClient.instance.insertEntertainment(entriBaru)

                if (response.isSuccessful && response.body()?.success == true) {
                    navigateBack()
                } else {
                    // Handle error jika perlu
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

// State untuk UI
data class EntryUiState(
    val detailEntri: DetailEntri = DetailEntri(),
    val isEntryValid: Boolean = false
)

// Data class khusus form (semua String biar gampang di TextField)
data class DetailEntri(
    val title: String = "",
    val description: String = "",
    val genre: String = "",
    val photo: String = "",
    val category: String = "watch", // Default
    val status: String = "planned", // Default
    val rating: String = "0.0"
)

// Fungsi Mapper dari Form ke Model Database
fun DetailEntri.toEntriHiburan(): EntriHiburan = EntriHiburan(
    id = 0, // ID biasanya auto-increment di DB
    title = title,
    description = description,
    genre = genre,
    photo = if (photo.isBlank()) null else photo,
    category = category,
    status = status,
    rating = rating.toDoubleOrNull() ?: 0.0
    // user_id harusnya dihandle di sini atau di backend session. 
    // Jika perlu kirim user_id, tambahkan field di EntriHiburan
)