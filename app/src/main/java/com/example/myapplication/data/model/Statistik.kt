package com.example.myapplication.data.model

import kotlinx.serialization.Serializable


// Digunakan oleh Kotlin Serialization saat parsing JSON dari API
@Serializable
data class StatistikResponse(
    val success: Boolean,
    val data: StatistikDataModel
)

// Data class untuk menampung detail statistik user
@Serializable
data class StatistikDataModel(
    val totalEntry: Int = 0,
    val averageRating: Double = 0.0,
    val favoriteGenre: String? = null, // Ini dari tabel statistics
    val genreDistribution: Map<String, Int> = emptyMap(),
    val statusDistribution: Map<String, Int> = emptyMap(),
    val categoryDistribution: Map<String, Int> = emptyMap()
)