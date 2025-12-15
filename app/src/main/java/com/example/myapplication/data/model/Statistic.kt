package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class Statistic(
    @SerializedName("total_entries")
    val totalEntries: Int,

    @SerializedName("completed_count")
    val completedCount: Int,

    @SerializedName("average_rating")
    val averageRating: Double,

    @SerializedName("favorite_genre")
    val favoriteGenre: String
)