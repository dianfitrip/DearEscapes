package com.example.myapplication.data.model

// Pastikan isinya persis seperti ini:
data class LoginResponse(
    val success: Boolean, // Ini yang dicari oleh AuthViewModel (dan merah di gambar Anda)
    val message: String,
    val data: UserData?
)

data class UserData(
    val id: Int,
    val username: String,
    val email: String
)

data class LoginRequest(
    val email: String,
    val password: String
)