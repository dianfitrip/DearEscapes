package com.example.myapplication.data.repository

import com.example.myapplication.data.apiservice.ApiService
import com.example.myapplication.data.model.EntertainmentResponse
import com.example.myapplication.data.model.EntriHiburan
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

// Interface Repository
interface EntriRepository {
    // Insert: Menerima file gambar (bisa null)
    suspend fun insertEntri(entriHiburan: EntriHiburan, imageFile: File?)

    // Get All: Mengambil semua data
    suspend fun getEntri(): Response<EntertainmentResponse>

    // Get Detail: Mengambil satu data berdasarkan ID
    suspend fun getEntriById(id: Int): EntriHiburan

    // Update: Mengedit data berdasarkan ID (Modifikasi Baru)
    suspend fun updateEntri(id: Int, entriHiburan: EntriHiburan, imageFile: File?)

    // Delete: Menghapus data berdasarkan ID
    suspend fun deleteEntri(id: Int)
}

// Implementasi Repository
class NetworkEntriRepository(
    private val apiService: ApiService
) : EntriRepository {

    override suspend fun insertEntri(entriHiburan: EntriHiburan, imageFile: File?) {
        // 1. Buat RequestBody untuk teks
        val userIdRB = entriHiburan.userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val titleRB = entriHiburan.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val descRB = entriHiburan.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val genreRB = entriHiburan.genre.toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryRB = entriHiburan.category.toRequestBody("text/plain".toMediaTypeOrNull())
        val statusRB = entriHiburan.status.toRequestBody("text/plain".toMediaTypeOrNull())
        val ratingRB = entriHiburan.rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        // 2. Buat MultipartBody.Part untuk gambar (jika ada)
        val photoPart = if (imageFile != null) {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", imageFile.name, requestFile)
        } else {
            null
        }

        // 3. Kirim ke API
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

    // --- IMPLEMENTASI MODIFIKASI UPDATE ---
    override suspend fun updateEntri(id: Int, entriHiburan: EntriHiburan, imageFile: File?) {
        // 1. Konversi data teks ke RequestBody
        val titleRB = entriHiburan.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val descRB = entriHiburan.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val genreRB = entriHiburan.genre.toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryRB = entriHiburan.category.toRequestBody("text/plain".toMediaTypeOrNull())
        val statusRB = entriHiburan.status.toRequestBody("text/plain".toMediaTypeOrNull())
        val ratingRB = entriHiburan.rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        // 2. Cek apakah ada gambar baru yang diupload
        val photoPart = if (imageFile != null) {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", imageFile.name, requestFile)
        } else {
            null
        }

        // 3. Panggil API Update
        // Note: Pastikan urutan parameter sesuai dengan ApiService Anda
        val response = apiService.updateEntertainment(
            id = id,
            title = titleRB,
            description = descRB,
            genre = genreRB,
            category = categoryRB,
            status = statusRB,
            rating = ratingRB,
            photo = photoPart
        )

        // 4. Cek Error HTTP
        if (!response.isSuccessful) {
            throw Exception("Gagal Update. Kode: ${response.code()}")
        }

        // 5. Cek Error dari Body API (misal success: false)
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
}