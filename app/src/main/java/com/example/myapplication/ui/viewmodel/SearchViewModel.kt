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

    // Search Query (Judul)
    var searchQuery by mutableStateOf("")
        private set

    // Filter Genre (API)
    var selectedGenre by mutableStateOf<String?>(null)
        private set

    // Search Genre (Lokal untuk Bottom Sheet)
    var genreSearchQuery by mutableStateOf("")
        private set

    private var searchJob: Job? = null

    // 1. Update text search judul (Auto-search debounce)
    fun updateQuery(newQuery: String) {
        searchQuery = newQuery
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(600) // Tunggu user selesai mengetik
            performSearch()
        }
    }

    // 2. Update text pencarian GENRE (Lokal di Bottom Sheet)
    fun updateGenreSearchQuery(query: String) {
        genreSearchQuery = query
    }

    // 3. Pilih Genre dari Bottom Sheet
    fun selectGenre(genre: String?) {
        // Jika genre yang sama diklik, batalkan pilihan (null)
        selectedGenre = if (selectedGenre == genre) null else genre
        // Reset pencarian genre lokal biar bersih saat dibuka lagi
        genreSearchQuery = ""
        // Langsung cari data baru
        performSearch()
    }

    fun performSearch() {
        viewModelScope.launch {
            uiState = SearchUiState.Loading
            try {
                val userId = userPreferences.getUserId.first()
                if (userId != null) {
                    // [PENTING] Kirim User ID, Search, dan Genre ke Repository
                    val response = repository.searchEntri(
                        userId = userId,
                        query = searchQuery.ifBlank { null },
                        genre = selectedGenre
                    )

                    if (response.isSuccessful && response.body()?.success == true) {
                        val data = response.body()!!.data
                        if (data.isEmpty()) uiState = SearchUiState.Empty
                        else uiState = SearchUiState.Success(data)
                    } else {
                        uiState = SearchUiState.Error("Gagal mencari data")
                    }
                }
            } catch (e: Exception) {
                uiState = SearchUiState.Error("Koneksi error: ${e.message}")
            }
        }
    }

    // Init awal untuk memuat data default
    init {
        performSearch()
    }
}