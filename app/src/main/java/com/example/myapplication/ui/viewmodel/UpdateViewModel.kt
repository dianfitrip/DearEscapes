package com.example.myapplication.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.EntriHiburan
import com.example.myapplication.data.repository.EntriRepository
import com.example.myapplication.utils.FileUtils
import kotlinx.coroutines.launch
import java.io.File

class UpdateViewModel(
    private val repository: EntriRepository
) : ViewModel() {

    var uiState by mutableStateOf(EntryUiState())
        private set

    // Load data lama berdasarkan ID
    fun loadEntri(id: Int) {
        viewModelScope.launch {
            try {
                val entri = repository.getEntriById(id)
                uiState = EntryUiState(
                    detailEntri = entri.toDetailEntri(),
                    isEntryValid = true
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUiState(newDetail: DetailEntri) {
        uiState = EntryUiState(detailEntri = newDetail, isEntryValid = validasiInput(newDetail))
    }

    private fun validasiInput(uiState: DetailEntri = this.uiState.detailEntri): Boolean {
        return uiState.title.isNotBlank() && uiState.description.isNotBlank()
    }

    fun updateEntri(id: Int, context: Context, navigateBack: () -> Unit) {
        viewModelScope.launch {
            // [MODIFIKASI] Validasi Input dengan Toast
            if (!validasiInput()) {
                Toast.makeText(context, "Mohon isi Judul dan Deskripsi!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val detail = uiState.detailEntri

                // Cek apakah user mengganti foto?
                var imageFile: File? = null
                if (detail.photo.startsWith("content://")) {
                    val uri = android.net.Uri.parse(detail.photo)
                    imageFile = FileUtils.getFileFromUri(context, uri)
                }

                // Convert UI State ke Model Data
                val entriUpdate = detail.toEntriHiburan(0)

                repository.updateEntri(id, entriUpdate, imageFile)

                // [MODIFIKASI] Toast Sukses
                Toast.makeText(context, "Data Berhasil Diperbarui!", Toast.LENGTH_SHORT).show()

                navigateBack()
            } catch (e: Exception) {
                // [MODIFIKASI] Toast Error
                e.printStackTrace()
                Toast.makeText(context, "Gagal update: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

// Extension function untuk convert Data Database -> UI State
fun EntriHiburan.toDetailEntri(): DetailEntri = DetailEntri(
    title = title,
    description = description,
    genre = genre,
    photo = photo ?: "",
    category = category,
    status = status,
    rating = rating.toString()
)