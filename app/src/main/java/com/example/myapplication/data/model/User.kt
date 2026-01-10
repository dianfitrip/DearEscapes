package com.example.myapplication.data.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    // Field ini opsional, tergantung apakah API mengirimkan data kapan akun dibuat
    @SerializedName("created_at")
    val createdAt: String? = null
)
