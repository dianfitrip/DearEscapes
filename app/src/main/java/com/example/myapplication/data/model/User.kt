package com.example.myapplication.data.model

// Data class ini merepresentasikan data user yang diterima dari backend
import com.google.gson.annotations.SerializedName

data class User(
    // Dipetakan dari field JSON "id"
    @SerializedName("id")
    val id: Int,
    // Dipetakan dari field JSON "username"
    @SerializedName("username")
    val username: String,
    // Dipetakan dari field JSON "email"
    @SerializedName("email")
    val email: String,
    // Dipetakan dari field JSON "created_at" bisa null jika backend tidak mengirim field ini
    @SerializedName("created_at")
    val createdAt: String? = null
)
