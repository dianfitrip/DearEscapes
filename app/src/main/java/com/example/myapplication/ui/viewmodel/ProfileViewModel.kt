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

// Data class untuk menampung hasil hitungan statistik di UI
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

    // Mengambil Username secara realtime
    val username: StateFlow<String> = userPreferences.getUsername
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Loading..."
        )

    // Mengambil Email secara realtime
    val email: StateFlow<String> = userPreferences.getEmail
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Loading..."
        )

    // StateFlow untuk menyimpan data statistik yang akan ditampilkan di HalamanProfil
    private val _uiStats = MutableStateFlow(ProfileStats())
    val uiStats: StateFlow<ProfileStats> = _uiStats.asStateFlow()

    init {
        fetchUserProfile()
    }

    /**
     * Fungsi untuk mengambil data entri milik user dan menghitung statistiknya.
     * [MODIFIKASI] Ubah menjadi PUBLIC (tanpa 'private') agar bisa dipanggil dari UI
     */
    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                // 1. Ambil User ID dari sesi login (DataStore)
                val userId = userPreferences.getUserId.first()

                // Pastikan userId valid
                if (userId != null && userId != 0) {

                    // 2. Panggil API searchEntri (Menggunakan endpoint yang sudah support filter by ID)
                    val response = repository.searchEntri(
                        userId = userId,
                        query = null,
                        genre = null
                    )

                    if (response.isSuccessful && response.body()?.success == true) {
                        // Data yang diterima dari server (List<EntriHiburan>)
                        val myData = response.body()!!.data

                        // 3. Hitung Statistik berdasarkan field 'status'
                        val planned = myData.count { it.status.equals("planned", ignoreCase = true) }
                        val inProgress = myData.count { it.status.equals("in_progress", ignoreCase = true) }
                        val completed = myData.count { it.status.equals("completed", ignoreCase = true) }
                        val dropped = myData.count { it.status.equals("dropped", ignoreCase = true) }

                        // 4. Update UI State dengan hasil hitungan
                        _uiStats.value = ProfileStats(
                            planned = planned,
                            inProgress = inProgress,
                            completed = completed,
                            dropped = dropped,
                            total = myData.size
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle error silent
                e.printStackTrace()
            }
        }
    }

    /**
     * Fungsi Logout
     */
    fun logout() {
        viewModelScope.launch {
            userPreferences.clearSession()
        }
    }
}