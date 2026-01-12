package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class EntriResponse(
    // Menangkap status success: true/false dari backend
    @SerializedName("success") val success: Boolean,

    // Menangkap pesan (jika ada)
    @SerializedName("message") val message: String? = null,

    // Menangkap list data hiburan (ini yang paling penting)
    @SerializedName("data") val data: List<EntriHiburan>
)