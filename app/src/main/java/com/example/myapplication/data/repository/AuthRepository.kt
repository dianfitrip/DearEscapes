package com.example.myapplication.data.repository


import com.example.myapplication.data.apiservice.ApiService
import com.example.myapplication.data.model.RegisterRequest
import com.example.myapplication.data.model.RegisterResponse
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    // Fungsi untuk registrasi yang akan dipanggil oleh ViewModel
    suspend fun registerUser(request: RegisterRequest): Response<RegisterResponse> {
        return apiService.registerUser(request)
    }
}