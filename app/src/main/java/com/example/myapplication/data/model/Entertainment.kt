package com.example.myapplication.data.model


import com.google.gson.annotations.SerializedName

data class Entertainment(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("genre")
    val genre: String,

    @SerializedName("photo")
    val photoUrl: String?, // URL gambar dari server

    @SerializedName("category")
    val category: String, // 'watch' atau 'read'

    @SerializedName("status")
    val status: String,

    @SerializedName("rating")
    val rating: Double
)
