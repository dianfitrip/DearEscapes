package com.example.myapplication.data.repository

import com.example.myapplication.data.apiservice.ApiService
import com.example.myapplication.data.model.EntertainmentResponse
import com.example.myapplication.data.model.EntriResponse
import com.example.myapplication.data.model.EntriHiburan
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

interface EntriRepository {
    // Insert data hiburan baru (dengan atau tanpa gambar)
    suspend fun insertEntri(entriHiburan: EntriHiburan, imageFile: File?)
    // Ambil semua data hiburan
    suspend fun getEntri(): Response<EntertainmentResponse>
    // Ambil detail hiburan berdasarkan ID
    suspend fun getEntriById(id: Int): EntriHiburan
    // Update data hiburan (dengan atau tanpa gambar baru)
    suspend fun updateEntri(id: Int, entriHiburan: EntriHiburan, imageFile: File?)
    // Hapus data hiburan berdasarkan ID
    suspend fun deleteEntri(id: Int)
    // Pencarian hiburan berdasarkan userId, query judul, dan genre
    suspend fun searchEntri(userId: Int, query: String?, genre: String?): Response<EntriResponse>
}


// Repository ini mengimplementasikan EntriRepository dan menggunakan ApiService (Retrofit) sebagai sumber data
class NetworkEntriRepository(
    private val apiService: ApiService
) : EntriRepository {
    // Konversi setiap field teks menjadi RequestBody
    override suspend fun insertEntri(entriHiburan: EntriHiburan, imageFile: File?) {
        val userIdRB = entriHiburan.userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val titleRB = entriHiburan.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val descRB = entriHiburan.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val genreRB = entriHiburan.genre.toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryRB = entriHiburan.category.toRequestBody("text/plain".toMediaTypeOrNull())
        val statusRB = entriHiburan.status.toRequestBody("text/plain".toMediaTypeOrNull())
        val ratingRB = entriHiburan.rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val photoPart = if (imageFile != null) {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", imageFile.name, requestFile)
        } else {
            null
        }

        val response = apiService.insertEntertainment(
            userId = userIdRB,
            title = titleRB,
            description = descRB,
            genre = genreRB,
            category = categoryRB,
            status = statusRB,
            rating = ratingRB,
            photo = photoPart
        )

        if (!response.isSuccessful) {
            throw Exception("Gagal Insert. Kode: ${response.code()}")
        }

        val body = response.body()
        if (body != null && !body.success) {
            throw Exception(body.message)
        }
    }

    override suspend fun getEntri(): Response<EntertainmentResponse> {
        return apiService.getEntertainments()
    }

    override suspend fun getEntriById(id: Int): EntriHiburan {
        val response = apiService.getEntertainmentById(id)

        if (!response.isSuccessful) {
            throw Exception("Gagal ambil data. Kode: ${response.code()}")
        }

        val body = response.body()
        if (body != null && body.success) {
            return body.data
        } else {
            throw Exception(body?.message ?: "Unknown Error")
        }
    }

    override suspend fun updateEntri(id: Int, entriHiburan: EntriHiburan, imageFile: File?) {
        // 1. Konversi data teks ke RequestBody
        val titleRB = entriHiburan.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val descRB = entriHiburan.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val genreRB = entriHiburan.genre.toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryRB = entriHiburan.category.toRequestBody("text/plain".toMediaTypeOrNull())
        val statusRB = entriHiburan.status.toRequestBody("text/plain".toMediaTypeOrNull())
        val ratingRB = entriHiburan.rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        // 2. Buat RequestBody untuk photo_string (foto lama dari server)
        val photoStringRB = if (!entriHiburan.photo.isNullOrEmpty()) {
            entriHiburan.photo.toRequestBody("text/plain".toMediaTypeOrNull())
        } else {
            null
        }

        // 3. untuk Cek apakah ada gambar baru yang diupload
        val photoPart = if (imageFile != null) {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", imageFile.name, requestFile)
        } else {
            null
        }

        // 4. Panggil API Update dengan DUA parameter foto:
        //    - photo: MultipartBody.Part? (foto baru sebagai file)
        //    - photo_string: RequestBody? (string path foto lama)
        val response = apiService.updateEntertainment(
            id = id,
            title = titleRB,
            description = descRB,
            genre = genreRB,
            category = categoryRB,
            status = statusRB,
            rating = ratingRB,
            photo = photoPart,
            photoString = photoStringRB
        )

        // 5. Cek Error HTTP
        if (!response.isSuccessful) {
            throw Exception("Gagal Update. Kode: ${response.code()}")
        }

        // 6. Cek Error dari Body API
        val body = response.body()
        if (body != null && !body.success) {
            throw Exception(body.message)
        }
    }

    override suspend fun deleteEntri(id: Int) {
        val response = apiService.deleteEntertainment(id)

        if (!response.isSuccessful) {
            throw Exception("Gagal menghapus. Kode: ${response.code()}")
        }

        val body = response.body()
        if (body != null && !body.success) {
            throw Exception(body.message)
        }
    }

    override suspend fun searchEntri(userId: Int, query: String?, genre: String?): Response<EntriResponse> {
        return apiService.searchEntertainments(userId, query, genre)
    }
}