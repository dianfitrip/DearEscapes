package com.example.myapplication.data.apiservice

import com.example.myapplication.data.model.EntertainmentDetailResponse
import com.example.myapplication.data.model.EntertainmentResponse
import com.example.myapplication.data.model.LoginRequest
import com.example.myapplication.data.model.LoginResponse
import com.example.myapplication.data.model.RegisterRequest
import com.example.myapplication.data.model.RegisterResponse
import com.example.myapplication.data.model.UpdateProfileRequest
import com.example.myapplication.data.model.StatistikResponse
import retrofit2.http.Query
import com.example.myapplication.data.model.EntriResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    // --- AUTH ---
    @POST("register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    // --- GET DATA ---
    @GET("entertainments")
    suspend fun getEntertainments(): Response<EntertainmentResponse>

    @GET("entertainments/{id}")
    suspend fun getEntertainmentById(@Path("id") id: Int): Response<EntertainmentDetailResponse>

    @DELETE("delete-entertainment/{id}")
    suspend fun deleteEntertainment(@Path("id") id: Int): Response<EntertainmentDetailResponse>

    @PUT("update-user/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: UpdateProfileRequest
    ): Response<RegisterResponse>

    @GET("statistics/{userId}")
    suspend fun getUserStatistics(@Path("userId") userId: Int): Response<StatistikResponse>

    @GET("entertainments")
    suspend fun searchEntertainments(
        @Query("user_id") userId: Int,
        @Query("search") search: String? = null,
        @Query("genre") genre: String? = null
    ): Response<EntriResponse>

    // --- INSERT ---
    @Multipart
    @POST("insert-entertainment")
    suspend fun insertEntertainment(
        @Part("user_id") userId: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("category") category: RequestBody,
        @Part("status") status: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<EntertainmentDetailResponse>

    // --- UPDATE (DITAMBAHKAN PARAMETER photo_string) ---
    @Multipart
    @PUT("update-entertainment/{id}")
    suspend fun updateEntertainment(
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("category") category: RequestBody,
        @Part("status") status: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part photo: MultipartBody.Part?,
        @Part("photo_string") photoString: RequestBody? = null // PARAMETER BARU untuk foto lama
    ): Response<EntertainmentDetailResponse>
}