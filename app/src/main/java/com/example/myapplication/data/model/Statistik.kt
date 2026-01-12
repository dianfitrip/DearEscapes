package com.example.myapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StatistikResponse(
    val success: Boolean,
    val data: StatistikDataModel
)

@Serializable
data class StatistikDataModel(
    val totalEntry: Int = 0,
    val averageRating: Double = 0.0,
    val favoriteGenre: String? = null, // Ini dari tabel statistics
    val genreDistribution: Map<String, Int> = emptyMap(),
    val statusDistribution: Map<String, Int> = emptyMap(),
    val categoryDistribution: Map<String, Int> = emptyMap()
)