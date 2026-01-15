package com.example.myapplication.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.EntriHiburan
import com.example.myapplication.data.repository.EntriRepository
import com.example.myapplication.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class UpdateViewModel(
    private val repository: EntriRepository
) : ViewModel() {

    var uiState by mutableStateOf(EntryUiState())
        private set

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoadingPhoto = MutableStateFlow(false)
    val isLoadingPhoto: StateFlow<Boolean> = _isLoadingPhoto.asStateFlow()

    // Cache untuk foto yang sudah di-download
    private val downloadedPhotos = mutableMapOf<String, String>()

    // Load data lama berdasarkan ID - DENGAN CONTEXT
    fun loadEntri(id: Int, context: Context? = null) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                _isLoadingPhoto.value = true

                val entri = repository.getEntriById(id)

                // Konversi path foto untuk tampilan di UI
                val photoForUI = if (!entri.photo.isNullOrEmpty()) {
                    processPhotoForUI(entri.photo!!, context)
                } else {
                    ""
                }

                uiState = EntryUiState(
                    detailEntri = entri.toDetailEntri().copy(photo = photoForUI),
                    isEntryValid = true
                )

                Log.d("UpdateViewModel", "Load entri berhasil. ID: $id, Photo URL: $photoForUI")

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Gagal memuat data: ${e.message}"
                Log.e("UpdateViewModel", "Load entri gagal: ${e.message}")
            } finally {
                _isLoadingPhoto.value = false
            }
        }
    }

    // Fungsi untuk memproses foto untuk ditampilkan di UI
    private suspend fun processPhotoForUI(photoUrl: String, context: Context?): String {
        return withContext(Dispatchers.IO) {
            try {
                when {
                    // 1. Foto sudah dalam format URI lokal (content:// atau file://)
                    photoUrl.startsWith("content://") || photoUrl.startsWith("file://") -> {
                        Log.d("UpdateViewModel", "Foto URI lokal: $photoUrl")
                        photoUrl
                    }

                    // 2. Foto dari server (URL HTTP/HTTPS) - EMOJI FIX
                    photoUrl.startsWith("http://") || photoUrl.startsWith("https://") -> {
                        Log.d("UpdateViewModel", "Foto URL server: $photoUrl")

                        // Untuk emulator, ubah localhost ke 10.0.2.2
                        val processedUrl = if (photoUrl.contains("localhost:3000")) {
                            photoUrl.replace("localhost:3000", "10.0.2.2:3000")
                        } else {
                            photoUrl
                        }

                        // Jika ada context, coba download ke cache
                        context?.let { ctx ->
                            try {
                                // Cek cache dulu
                                if (downloadedPhotos.containsKey(processedUrl)) {
                                    val cachedUri = downloadedPhotos[processedUrl]
                                    Log.d("UpdateViewModel", "Menggunakan foto dari cache: $cachedUri")
                                    return@let cachedUri
                                }

                                // Download foto ke cache
                                val localUri = downloadImageToCache(ctx, processedUrl)
                                downloadedPhotos[processedUrl] = localUri.toString()

                                Log.d("UpdateViewModel", "Foto didownload ke: $localUri")
                                localUri.toString()
                            } catch (e: Exception) {
                                Log.e("UpdateViewModel", "Gagal download foto, fallback ke URL: ${e.message}")
                                processedUrl // Fallback ke URL
                            }
                        } ?: processedUrl // Jika tidak ada context, kembalikan URL
                    }

                    // 3. Foto dalam format uploads/filename.jpg (path relatif)
                    photoUrl.startsWith("uploads/") -> {
                        // Konversi ke URL lengkap untuk emulator
                        val url = "http://10.0.2.2:3000/$photoUrl"
                        Log.d("UpdateViewModel", "Foto uploads/ -> URL: $url")
                        url
                    }

                    // 4. Hanya nama file (misal: 1768485867129.jpg)
                    photoUrl.contains(".") && !photoUrl.contains("/") -> {
                        val url = "http://10.0.2.2:3000/uploads/$photoUrl"
                        Log.d("UpdateViewModel", "Foto nama file -> URL: $url")
                        url
                    }

                    // 5. Format lainnya atau kosong
                    else -> {
                        Log.d("UpdateViewModel", "Foto format tidak dikenali atau kosong: $photoUrl")
                        photoUrl
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdateViewModel", "Error processPhotoForUI: ${e.message}")
                photoUrl
            }
        }
    }

    // Fungsi untuk download image ke cache lokal
    private suspend fun downloadImageToCache(context: Context, imageUrl: String): Uri {
        return withContext(Dispatchers.IO) {
            try {
                // Buat koneksi HTTP
                val url = java.net.URL(imageUrl)
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.connect()

                if (connection.responseCode == 200) {
                    // Baca input stream
                    val inputStream = connection.inputStream
                    val bytes = inputStream.readBytes()
                    inputStream.close()
                    connection.disconnect()

                    // Buat file di cache directory
                    val cacheDir = context.cacheDir
                    val fileName = "temp_${System.currentTimeMillis()}.jpg"
                    val cacheFile = File(cacheDir, fileName)

                    // Tulis bytes ke file
                    FileOutputStream(cacheFile).use { fos ->
                        fos.write(bytes)
                    }

                    Log.d("UpdateViewModel", "Foto tersimpan di cache: ${cacheFile.absolutePath}")

                    // Untuk Android, gunakan FileProvider URI
                    Uri.fromFile(cacheFile)
                } else {
                    throw Exception("HTTP ${connection.responseCode}: ${connection.responseMessage}")
                }
            } catch (e: Exception) {
                Log.e("UpdateViewModel", "Download image error: ${e.message}")
                throw e
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
            // Validasi Input
            if (!validasiInput()) {
                _errorMessage.value = "Mohon isi Judul dan Deskripsi!"
                return@launch
            }

            try {
                val detail = uiState.detailEntri
                Log.d("UpdateViewModel", "Memulai update entri $id, foto: ${detail.photo}")

                // LOGIKA PENANGANAN FOTO UNTUK UPDATE
                var imageFile: File? = null
                var photoStringForServer = detail.photo

                when {
                    // 1. Foto baru dari gallery/perangkat (URI lokal)
                    detail.photo.startsWith("content://") || detail.photo.startsWith("file://") -> {
                        Log.d("UpdateViewModel", "Foto baru dari device: ${detail.photo}")
                        val uri = Uri.parse(detail.photo)
                        imageFile = FileUtils.getFileFromUri(context, uri)
                        photoStringForServer = "" // Foto baru akan diupload sebagai file
                    }

                    // 2. Foto dari cache (hasil download sebelumnya)
                    detail.photo.contains(context.cacheDir.absolutePath) -> {
                        Log.d("UpdateViewModel", "Foto dari cache lokal: ${detail.photo}")
                        val cacheFile = File(detail.photo)
                        if (cacheFile.exists()) {
                            imageFile = cacheFile
                            photoStringForServer = ""
                        }
                    }

                    // 3. Foto URL dari server - perlu ekstrak nama file
                    detail.photo.startsWith("http://10.0.2.2:3000/uploads/") -> {
                        Log.d("UpdateViewModel", "Foto URL server lokal: ${detail.photo}")
                        val fileName = detail.photo.substringAfterLast("/")
                        photoStringForServer = if (fileName.isNotEmpty()) "uploads/$fileName" else ""
                        imageFile = null // Tidak ada file baru
                    }

                    // 4. Foto sudah dalam format uploads/filename.jpg
                    detail.photo.startsWith("uploads/") -> {
                        Log.d("UpdateViewModel", "Foto format uploads/: ${detail.photo}")
                        photoStringForServer = detail.photo
                        imageFile = null
                    }

                    // 5. Tidak ada foto atau string kosong
                    detail.photo.isEmpty() -> {
                        Log.d("UpdateViewModel", "Tidak ada foto")
                        photoStringForServer = ""
                        imageFile = null
                    }

                    // 6. Format lainnya (pertahankan apa adanya)
                    else -> {
                        Log.d("UpdateViewModel", "Foto format lain: ${detail.photo}")
                        photoStringForServer = detail.photo
                        imageFile = null
                    }
                }

                // Convert UI State ke Model Data
                val entriUpdate = EntriHiburan(
                    id = 0,
                    userId = 0,
                    title = detail.title,
                    description = detail.description,
                    genre = detail.genre,
                    photo = photoStringForServer,
                    category = detail.category,
                    status = detail.status,
                    rating = detail.rating.toDoubleOrNull() ?: 0.0
                )

                Log.d("UpdateViewModel", "Mengupdate entri $id dengan data: $entriUpdate")

                // Panggil repository untuk update
                repository.updateEntri(id, entriUpdate, imageFile)

                _errorMessage.value = null
                Log.d("UpdateViewModel", "Update berhasil, navigasi kembali")

                // Beri jeda kecil sebelum navigasi
                kotlinx.coroutines.delay(500)

                navigateBack()

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Gagal update: ${e.message}"
                Log.e("UpdateViewModel", "Update gagal: ${e.message}")
            }
        }
    }

    // Fungsi untuk clear error message
    fun clearError() {
        _errorMessage.value = null
    }

    // Clear cache ketika tidak diperlukan lagi
    fun clearCache() {
        downloadedPhotos.clear()
        Log.d("UpdateViewModel", "Cache dibersihkan")
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