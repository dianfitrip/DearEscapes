package com.example.myapplication.ui.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.EntriHiburan
import com.example.myapplication.data.repository.EntriRepository
import com.example.myapplication.data.repository.UserPreferences
import com.example.myapplication.ui.utils.FileUtils
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

    // menyimpan data hiburan baru ke server
    fun saveEntry(context: Context, navigateBack: () -> Unit) {
        viewModelScope.launch {
            // 1. Cek Validasi dan Tampilkan Toast jika Gagal
            if (!validasiInput()) {
                Toast.makeText(context, "Mohon isi Judul dan Deskripsi!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                // 2. Ambil User ID
                val currentUserId = userPreferences.getUserId.first()

                if (currentUserId == null) {
                    Toast.makeText(context, "Sesi berakhir, silakan login ulang", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val detail = uiState.detailEntri

                // 3. Konversi URI ke File fisik (jika ada foto yang dipilih)
                var imageFile: File? = null
                if (detail.photo.isNotBlank()) {
                    try {
                        if (detail.photo.startsWith("content://")) {
                            val uri = android.net.Uri.parse(detail.photo)
                            imageFile = FileUtils.getFileFromUri(context, uri)
                        }
                    } catch (e: Exception) {
                        Log.e("EntryViewModel", "Gagal memproses file gambar: ${e.message}")
                    }
                }

                // 4. Siapkan data Entri
                val entriBaru = detail.toEntriHiburan(currentUserId)

                // 5. Panggil Repository
                repository.insertEntri(entriBaru, imageFile)

                Log.d("EntryViewModel", "Simpan Berhasil untuk User ID: $currentUserId")

                //Toast Sukses
                Toast.makeText(context, "Berhasil Menambahkan Hiburan!", Toast.LENGTH_SHORT).show()

                // Kembali ke halaman sebelumnya
                navigateBack()

            } catch (e: Exception) {
                //Toast Error
                Log.e("EntryViewModel", "Simpan Gagal: ${e.message}")
                e.printStackTrace()
                Toast.makeText(context, "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}



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