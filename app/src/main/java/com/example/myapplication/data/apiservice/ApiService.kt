package com.example.myapplication.data.apiservice

import com.example.myapplication.data.model.EntertainmentResponse
import com.example.myapplication.data.model.LoginRequest
import com.example.myapplication.data.model.LoginResponse
import com.example.myapplication.data.model.RegisterRequest
import com.example.myapplication.data.model.RegisterResponse
import com.example.myapplication.data.model.EntriHiburan
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("login")
    // PENTING: Pastikan bagian dalam kurung siku <LoginResponse> benar
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @GET("entertainments")
    suspend fun getEntertainments(): Response<EntertainmentResponse>

    @POST("insert-entertainment")
    suspend fun insertEntertainment(@Body entertainment: EntriHiburan): Response<EntertainmentResponse>
}