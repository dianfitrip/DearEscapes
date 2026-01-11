package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.EntriRepository
import com.example.myapplication.data.repository.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// State untuk menyimpan jumlah statistik
data class ProfileStats(
    val planned: Int = 0,
    val inProgress: Int = 0,
    val completed: Int = 0,
    val dropped: Int = 0,
    val total: Int = 0
)

class ProfileViewModel(
    private val userPreferences: UserPreferences,
    private val repository: EntriRepository
) : ViewModel() {

    val username: StateFlow<String> = userPreferences.getUsername
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Loading..."
        )

    val email: StateFlow<String> = userPreferences.getEmail
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Loading..."
        )

    // --- PERBAIKAN DI SINI ---
    // Hapus kode 'val stats = repository.getEntriFlow()...' yang error itu.
    // Kita pakai yang Manual Fetch di bawah ini saja.

    private val _uiStats = MutableStateFlow(ProfileStats())
    val uiStats: StateFlow<ProfileStats> = _uiStats.asStateFlow()

    fun fetchProfileData() {
        viewModelScope.launch {
            try {
                // Ambil User ID aktif
                val userId = userPreferences.getUserId.first() ?: return@launch

                // Ambil data dari API/DB
                val response = repository.getEntri()

                if (response.isSuccessful && response.body() != null) {
                    // Filter data milik user ini saja
                    val allData = response.body()!!.data.filter { it.userId == userId }

                    // Hitung statistik
                    val planned = allData.count { it.status.equals("planned", ignoreCase = true) }
                    val inProgress = allData.count { it.status.equals("in_progress", ignoreCase = true) }
                    val completed = allData.count { it.status.equals("completed", ignoreCase = true) }
                    val dropped = allData.count { it.status.equals("dropped", ignoreCase = true) }

                    // Update State UI
                    _uiStats.value = ProfileStats(
                        planned = planned,
                        inProgress = inProgress,
                        completed = completed,
                        dropped = dropped,
                        total = allData.size
                    )
                }
            } catch (e: Exception) {
                // Error handling (bisa ditambahkan log jika perlu)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearSession()
        }
    }
}