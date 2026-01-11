// File: EntriContainer.kt
package com.example.myapplication.data.repository

import android.content.Context // Import Context
import com.example.myapplication.data.apiservice.RetrofitClient

interface AppContainer {
    val entriRepository: EntriRepository
    val userPreferences: UserPreferences // Tambahkan ini
}

class EntriContainer(private val context: Context) : AppContainer { // Terima Context di sini

    private val apiService = RetrofitClient.instance

    override val entriRepository: EntriRepository by lazy {
        NetworkEntriRepository(apiService)
    }

    // Inisialisasi UserPreferences
    override val userPreferences: UserPreferences by lazy {
        UserPreferences(context)
    }
}