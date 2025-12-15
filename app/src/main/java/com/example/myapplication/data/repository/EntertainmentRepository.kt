package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.model.Entertainment
import com.example.myapplication.data.model.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class EntertainmentRepository(private val apiService: ApiService) {

    // 1. Fungsi untuk mengambil daftar hiburan (GET)
    suspend fun getEntertainments(): List<Entertainment> {
        return apiService.getEntertainments()
    }

    // 2. Fungsi untuk menambah data (POST)
    suspend fun addEntertainment(
        title: RequestBody,
        description: RequestBody,
        genre: RequestBody,
        category: RequestBody,
        status: RequestBody,
        rating: RequestBody,
        photo: MultipartBody.Part?
    ): LoginResponse {
        return apiService.addEntertainment(title, description, genre, category, status, rating, photo)
    }

    // 3. Pola Singleton (Cukup SATU kali saja)
    companion object {
        @Volatile
        private var instance: EntertainmentRepository? = null

        fun getInstance(apiService: ApiService): EntertainmentRepository =
            instance ?: synchronized(this) {
                instance ?: EntertainmentRepository(apiService).also { instance = it }
            }
    }

    // Fungsi Ambil Detail
    suspend fun getEntertainmentDetail(id: Int): Entertainment {
        return apiService.getEntertainmentDetail(id)
    }

    suspend fun updateEntertainment(
        id: Int,
        title: RequestBody,
        description: RequestBody,
        genre: RequestBody,
        category: RequestBody,
        status: RequestBody,
        rating: RequestBody,
        photo: MultipartBody.Part?
    ): LoginResponse {
        return apiService.updateEntertainment(id, title, description, genre, category, status, rating, photo)
    }
}