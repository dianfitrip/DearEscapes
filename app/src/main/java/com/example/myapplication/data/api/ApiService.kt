package com.example.myapplication.data.api

import com.example.myapplication.data.model.Entertainment
import com.example.myapplication.data.model.LoginRequest
import com.example.myapplication.data.model.LoginResponse
import com.example.myapplication.data.model.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    // --- AUTHENTICATION ---
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): LoginResponse

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    // --- ENTERTAINMENT (GET) ---
    @GET("entertainments")
    suspend fun getEntertainments(): List<Entertainment>

    // --- ENTERTAINMENT (POST / ADD) ---
    @Multipart
    @POST("entertainments")
    suspend fun addEntertainment(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("category") category: RequestBody,
        @Part("status") status: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part photo: MultipartBody.Part? // Boleh null jika user tidak upload foto
    ): LoginResponse


    @Multipart
    @PUT("entertainments/{id}") // Mengupdate data berdasarkan ID
    suspend fun updateEntertainment(
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("category") category: RequestBody,
        @Part("status") status: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part photo: MultipartBody.Part? // Foto bersifat opsional saat edit
    ): LoginResponse

    //statistic
    @GET("statistics") // Asumsi endpoint backend Anda adalah /statistics
    suspend fun getStatistics(): Statistic
}