package com.example.myapplication.data.model

// Data class untuk menampung response dari API login
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?
)

// Data class untuk menyimpan informasi user ketika berhasil login
data class UserData(
    val id: Int,
    val username: String,
    val email: String
)

// Data class untuk mengirim data login ke server
data class LoginRequest(
    val email: String,
    val password: String
)