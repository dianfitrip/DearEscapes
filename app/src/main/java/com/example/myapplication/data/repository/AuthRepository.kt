package com.example.myapplication.data.repository


import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.model.LoginRequest
import com.example.myapplication.data.model.LoginResponse
import com.example.myapplication.data.model.RegisterRequest

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(LoginRequest(email, password))
    }

    suspend fun register(name: String, email: String, password: String): LoginResponse {
        return apiService.register(RegisterRequest(name, email, password))
    }

    // Singleton pattern sederhana agar mudah diakses
    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(apiService: ApiService): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService).also { instance = it }
            }
    }
}