package com.example.myapplication.ui.screen.edit


import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Entertainment
import com.example.myapplication.data.repository.EntertainmentRepository
import com.example.myapplication.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EditViewModel(private val repository: EntertainmentRepository) : ViewModel() {

    // State untuk mengambil data lama
    private val _uiState = MutableStateFlow<UiState<Entertainment>>(UiState.Loading)
    val uiState: StateFlow<UiState<Entertainment>> = _uiState

    // State untuk proses update
    private val _updateState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val updateState: StateFlow<UiState<String>> = _updateState

    // 1. Ambil Data Lama
    fun getEntertainmentDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.getEntertainmentDetail(id)
                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Gagal memuat data")
            }
        }
    }

    // 2. Kirim Data Baru
    fun updateEntertainment(
        context: Context,
        id: Int,
        title: String,
        description: String,
        genre: String,
        category: String,
        status: String,
        rating: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            try {
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val genreBody = genre.toRequestBody("text/plain".toMediaTypeOrNull())
                val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
                val statusBody = status.toRequestBody("text/plain".toMediaTypeOrNull())
                val ratingBody = rating.toRequestBody("text/plain".toMediaTypeOrNull())

                var photoPart: MultipartBody.Part? = null
                imageUri?.let { uri ->
                    val file = uriToFile(uri, context)
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)
                }

                val response = repository.updateEntertainment(
                    id, titleBody, descBody, genreBody, categoryBody, statusBody, ratingBody, photoPart
                )

                if (!response.error) {
                    _updateState.value = UiState.Success(response.message)
                } else {
                    _updateState.value = UiState.Error(response.message)
                }
            } catch (e: Exception) {
                _updateState.value = UiState.Error(e.message ?: "Gagal Update")
            }
        }
    }

    // Helper function (sama seperti di AddViewModel)
    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val myFile = File.createTempFile("temp_edit_image", ".jpg", context.cacheDir)
        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    // Reset status update agar tidak toast berulang
    fun resetUpdateState() {
        _updateState.value = UiState.Loading
    }
}