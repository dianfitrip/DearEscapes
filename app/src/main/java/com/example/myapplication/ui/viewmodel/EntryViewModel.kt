package com.example.myapplication.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.EntriHiburan
import com.example.myapplication.data.repository.EntriRepository
import com.example.myapplication.data.repository.UserPreferences
import com.example.myapplication.utils.FileUtils // Pastikan FileUtils sudah dibuat di package utils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class EntryViewModel(
    private val repository: EntriRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var uiState by mutableStateOf(EntryUiState())
        private set

    fun updateUiState(newDetail: DetailEntri) {
        uiState = EntryUiState(detailEntri = newDetail, isEntryValid = validasiInput(newDetail))
    }

    private fun validasiInput(uiState: DetailEntri = this.uiState.detailEntri): Boolean {
        return uiState.title.isNotBlank() && uiState.description.isNotBlank()
    }

    // FUNGSI INI TELAH DIMODIFIKASI UNTUK UPLOAD GAMBAR
    fun saveEntry(context: Context, navigateBack: () -> Unit) {
        viewModelScope.launch {
            if (validasiInput()) {
                try {
                    // 1. Ambil User ID dari Preferences
                    val currentUserId = userPreferences.getUserId.first()

                    if (currentUserId == null) {
                        Log.e("EntryViewModel", "User ID tidak ditemukan")
                        return@launch
                    }

                    val detail = uiState.detailEntri

                    // 2. Konversi URI ke File fisik (jika ada foto yang dipilih)
                    var imageFile: File? = null
                    if (detail.photo.isNotBlank()) {
                        try {
                            // Cek apakah ini URI content (dari galeri)
                            if (detail.photo.startsWith("content://")) {
                                val uri = android.net.Uri.parse(detail.photo)
                                imageFile = FileUtils.getFileFromUri(context, uri)
                            } else {
                                // Opsional: Handle jika path file biasa (jarang terjadi di modern Android storage)
                                // imageFile = File(detail.photo)
                            }
                        } catch (e: Exception) {
                            Log.e("EntryViewModel", "Gagal memproses file gambar: ${e.message}")
                        }
                    }

                    // 3. Siapkan data Entri (photo string diabaikan di sini karena dikirim sebagai file terpisah)
                    val entriBaru = detail.toEntriHiburan(currentUserId)

                    // 4. Panggil Repository dengan File Gambar
                    // Pastikan EntriRepository.insertEntri sudah menerima parameter (EntriHiburan, File?)
                    repository.insertEntri(entriBaru, imageFile)

                    Log.d("EntryViewModel", "Simpan Berhasil untuk User ID: $currentUserId")

                    // 5. Kembali ke halaman sebelumnya jika sukses
                    navigateBack()

                } catch (e: Exception) {
                    Log.e("EntryViewModel", "Simpan Gagal: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }
}

// --- DATA CLASSES & MAPPER ---

data class EntryUiState(
    val detailEntri: DetailEntri = DetailEntri(),
    val isEntryValid: Boolean = false
)

data class DetailEntri(
    val title: String = "",
    val description: String = "",
    val genre: String = "",
    val photo: String = "",
    val category: String = "watch",
    val status: String = "planned",
    val rating: String = "0.0"
)

fun DetailEntri.toEntriHiburan(userId: Int): EntriHiburan = EntriHiburan(
    id = 0,
    userId = userId,
    title = title,
    description = description,
    genre = genre,
    photo = if (photo.isBlank()) null else photo,
    category = category,
    status = status,
    rating = rating.toDoubleOrNull() ?: 0.0
)