package com.example.myapplication.data.model

// Data class untuk mengirim data pendaftaran (register) ke server
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

// Data class untuk menampung response dari server setelah proses register
data class RegisterResponse(
    val message: String,
    val userId: Int?
)