package com.example.myapplication.data.model

import kotlinx.serialization.Serializable

// Digunakan saat mengirim request update profil ke backend.
@Serializable
data class UpdateProfileRequest(
    val username: String,
    val email: String,
    val password: String? = null
)