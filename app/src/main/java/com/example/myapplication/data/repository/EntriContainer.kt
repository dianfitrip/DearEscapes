package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.apiservice.RetrofitClient


// untuk menyediakan dependency yang digunakan di aplikasi
interface AppContainer {
    // Repository untuk mengelola data Entri (hiburan)
    val entriRepository: EntriRepository
    // UserPreferences untuk menyimpan data sesi user (id, username, email)
    val userPreferences: UserPreferences
}

class EntriContainer(private val context: Context) : AppContainer {

    // Mengambil instance ApiService dari RetrofitClient
    private val apiService = RetrofitClient.instance

    // Inisialisasi EntriRepository
    override val entriRepository: EntriRepository by lazy {
        NetworkEntriRepository(apiService)
    }

    // Inisialisasi UserPreferences
    override val userPreferences: UserPreferences by lazy {
        UserPreferences(context)
    }
}