package com.example.myapplication.ui.viewmodel

import android.util.Log
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
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val data: List<EntriHiburan>) : SearchUiState
    data class Error(val message: String) : SearchUiState
    object Empty : SearchUiState
}

class SearchViewModel(
    private val repository: EntriRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var uiState: SearchUiState by mutableStateOf(SearchUiState.Idle)
        private set

    // Query pencarian (berdasarkan Judul)
    var searchQuery by mutableStateOf("")
        private set

    // Genre yang dipilih dari Bottom Sheet
    var selectedGenre by mutableStateOf<String?>(null)
        private set

    // Query untuk memfilter daftar genre di dalam Bottom Sheet (Lokal)
    var genreSearchQuery by mutableStateOf("")
        private set

    private var searchJob: Job? = null

    /**
     * Fungsi ini dipanggil setiap kali user mengetik di TextField Search.
     * Menggunakan debounce 500ms agar tidak terlalu sering menembak API saat user mengetik.
     */
    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery

        // Batalkan job pencarian sebelumnya jika user masih mengetik
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Tunggu 0.5 detik setelah user berhenti mengetik
            performSearch()
        }
    }

    /**
     * Fungsi untuk memfilter teks pencarian genre di dalam Bottom Sheet
     */
    fun onGenreSearchQueryChange(newQuery: String) {
        genreSearchQuery = newQuery
    }

    /**
     * Fungsi saat genre dipilih atau dibatalkan
     */
    fun selectGenre(genre: String?) {
        // Jika genre yang sama diklik lagi, batalkan pilihan (toggle null)
        selectedGenre = if (selectedGenre == genre) null else genre

        // Bersihkan filter pencarian genre lokal
        genreSearchQuery = ""

        // Langsung jalankan pencarian ke API
        performSearch()
    }

    /**
     * Fungsi Inti untuk mengambil data dari API
     */
    fun performSearch() {
        viewModelScope.launch {
            uiState = SearchUiState.Loading
            try {
                // Ambil User ID dari DataStore/Preferences secara realtime
                val userId = userPreferences.getUserId.first()

                if (userId != null && userId != 0) {
                    Log.d("SearchVM", "Requesting - UID: $userId, Q: $searchQuery, G: $selectedGenre")

                    // Memanggil repository
                    // Pastikan di EntriRepository.kt nama parameternya sesuai
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
                        Log.e("SearchVM", "API Error: ${response.message()}")
                        uiState = SearchUiState.Error("Gagal memuat data dari server")
                    }
                } else {
                    uiState = SearchUiState.Error("User ID tidak ditemukan. Silakan login ulang.")
                }
            } catch (e: Exception) {
                Log.e("SearchVM", "Exception: ${e.message}")
                uiState = SearchUiState.Error("Masalah koneksi: ${e.localizedMessage}")
            }
        }
    }

    init {
        // Load data awal (milik user tersebut) saat halaman pertama kali dibuka
        performSearch()
    }
}