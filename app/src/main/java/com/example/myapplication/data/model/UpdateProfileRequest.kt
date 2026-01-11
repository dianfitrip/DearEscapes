package com.example.myapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val username: String,
    val email: String,
    val password: String? = null
)