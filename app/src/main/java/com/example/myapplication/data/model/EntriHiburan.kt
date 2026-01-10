package com.example.myapplication.data.model



data class EntriHiburan(
    val id: Int,
    val title: String,
    val description: String,
    val genre: String,
    val photo: String?, // URL atau path foto
    val category: String, // 'watch' atau 'read'
    val status: String,
    val rating: Double
)

data class EntertainmentResponse(
    val success: Boolean,
    val message: String,
    val data: List<EntriHiburan>
)