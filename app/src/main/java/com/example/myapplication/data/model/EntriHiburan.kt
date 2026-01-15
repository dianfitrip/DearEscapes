package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class EntriHiburan(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val title: String,
    val description: String,
    val genre: String,
    val photo: String?,
    val category: String,
    val status: String,
    val rating: Double
)

// Response untuk GET ALL (Home) - Tetap List
data class EntertainmentResponse(
    val success: Boolean,
    val message: String,
    val data: List<EntriHiburan>
)

// Response untuk INSERT/UPDATE (Single Data) - Bukan List
data class EntertainmentDetailResponse(
    val success: Boolean,
    val message: String,
    val data: EntriHiburan
)