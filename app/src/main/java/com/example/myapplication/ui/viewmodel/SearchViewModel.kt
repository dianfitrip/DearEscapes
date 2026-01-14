package com.example.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.EntriHiburan
import com.example.myapplication.data.repository.EntriRepository
import com.example.myapplication.data.repository.UserPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    object Idle : SearchUiState    // Status awal (Kosong / Belum mencari)
    object Loading : SearchUiState // Sedang memuat
    data class Success(val data: List<EntriHiburan>) : SearchUiState // Ada data
    data class Error(val message: String) : SearchUiState // Error
    object Empty : SearchUiState   // Pencarian dilakukan tapi tidak ada hasil
}

class SearchViewModel(
    private val repository: EntriRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var uiState: SearchUiState by mutableStateOf(SearchUiState.Idle)
        private set

    // Query pencarian (Judul)
    var searchQuery by mutableStateOf("")
        private set

    // Genre yang dipilih
    var selectedGenre by mutableStateOf<String?>(null)
        private set

    // Query untuk search di dalam list genre (Bottom Sheet)
    var genreSearchQuery by mutableStateOf("")
        private set

    private var searchJob: Job? = null

    /**
     * Dipanggil saat user mengetik di search bar
     */
    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery

        // Batalkan pencarian sebelumnya jika user masih mengetik
        searchJob?.cancel()

        // [MODIFIKASI] Jika kolom kosong dan tidak ada genre dipilih,
        // kembalikan ke tampilan awal (Idle) jangan cari data.
        if (newQuery.isBlank() && selectedGenre == null) {
            uiState = SearchUiState.Idle
            return
        }

        // Debounce: Tunggu 0.5 detik sebelum request ke server
        searchJob = viewModelScope.launch {
            delay(500)
            performSearch()
        }
    }

    /**
     * Dipanggil saat user mengetik filter genre di Bottom Sheet
     */
    fun onGenreSearchQueryChange(newQuery: String) {
        genreSearchQuery = newQuery
    }

    /**
     * Dipanggil saat user memilih Genre
     */
    fun selectGenre(genre: String?) {
        selectedGenre = if (selectedGenre == genre) null else genre
        genreSearchQuery = "" // Reset pencarian lokal genre

        // [MODIFIKASI] Jika genre dimatikan dan search bar juga kosong, kembali ke Idle
        if (selectedGenre == null && searchQuery.isBlank()) {
            uiState = SearchUiState.Idle
        } else {
            // Jika ada genre atau ada teks, lakukan pencarian
            performSearch()
        }
    }

    /**
     * Eksekusi pencarian ke API
     */
    fun performSearch() {
        viewModelScope.launch {
            uiState = SearchUiState.Loading
            try {
                val userId = userPreferences.getUserId.first()
                if (userId != null && userId != 0) {

                    val response = repository.searchEntri(
                        userId = userId,
                        query = searchQuery.ifBlank { null },
                        genre = selectedGenre
                    )

                    if (response.isSuccessful && response.body()?.success == true) {
                        val data = response.body()!!.data
                        if (data.isEmpty()) {
                            uiState = SearchUiState.Empty
                        } else {
                            uiState = SearchUiState.Success(data)
                        }
                    } else {
                        uiState = SearchUiState.Error("Gagal memuat data")
                    }
                } else {
                    uiState = SearchUiState.Error("User tidak ditemukan")
                }
            } catch (e: Exception) {
                uiState = SearchUiState.Error("Koneksi bermasalah: ${e.localizedMessage}")
            }
        }
    }
}