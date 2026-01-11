package com.example.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.EntriRepository
import com.example.myapplication.data.repository.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Model data khusus untuk UI Statistik
data class StatistikData(
    val totalEntry: Int = 0,
    val averageRating: Double = 0.0,
    val genreDistribution: Map<String, Int> = emptyMap(),
    val statusDistribution: Map<String, Int> = emptyMap(),
    // [MODIFIKASI] Tambahkan Map baru untuk distribusi kategori (Watch vs Read)
    val categoryDistribution: Map<String, Int> = emptyMap()
)

sealed interface StatistikUiState {
    object Loading : StatistikUiState
    data class Success(val data: StatistikData) : StatistikUiState
    data class Error(val message: String) : StatistikUiState
}

class StatistikViewModel(
    private val repository: EntriRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var uiState: StatistikUiState by mutableStateOf(StatistikUiState.Loading)
        private set

    fun fetchStatistik() {
        viewModelScope.launch {
            uiState = StatistikUiState.Loading
            try {
                val currentUserId = userPreferences.getUserId.first()
                if (currentUserId == null) {
                    uiState = StatistikUiState.Error("User tidak ditemukan")
                    return@launch
                }

                val response = repository.getEntri()
                if (response.isSuccessful && response.body()?.success == true) {
                    val allData = response.body()!!.data.filter { it.userId == currentUserId }

                    if (allData.isEmpty()) {
                        uiState = StatistikUiState.Success(StatistikData())
                    } else {
                        // --- LOGIKA AGREGASI DATA (ALGORITMA STATISTIK) ---

                        // 1. Total Entry
                        val total = allData.size

                        // 2. Rata-rata Rating
                        val avgRating = allData.map { it.rating }.average()

                        // 3. Distribusi Genre (Group by Genre -> Count)
                        val genreDist = allData.groupingBy { it.genre }
                            .eachCount()
                            .toList()
                            .sortedByDescending { (_, value) -> value } // Urutkan dari terbanyak
                            .toMap()

                        // 4. Distribusi Status (Planned, In Progress, dll)
                        val statusDist = allData.groupingBy { it.status }
                            .eachCount()

                        // [MODIFIKASI] 5. Distribusi Kategori (Menonton vs Membaca)
                        val categoryDist = allData.groupingBy { it.category.lowercase() }
                            .eachCount()

                        uiState = StatistikUiState.Success(
                            StatistikData(
                                totalEntry = total,
                                averageRating = avgRating,
                                genreDistribution = genreDist,
                                statusDistribution = statusDist,
                                // Sertakan hasil agregasi kategori
                                categoryDistribution = categoryDist
                            )
                        )
                    }
                } else {
                    uiState = StatistikUiState.Error("Gagal memuat data")
                }
            } catch (e: Exception) {
                uiState = StatistikUiState.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }
}