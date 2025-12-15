package com.example.myapplication.data.api


import com.example.myapplication.data.model.LoginRequest
import com.example.myapplication.data.model.LoginResponse
import com.example.myapplication.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register") // Pastikan endpoint ini sama dengan di Backend Node.js Anda
    suspend fun register(
        @Body request: RegisterRequest
    ): LoginResponse // Kita pakai LoginResponse jika format reply-nya mirip (message/error)

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}