package com.example.myapplication.data.api


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
            // Level BODY agar kita bisa melihat isi request & response di Logcat (penting buat debugging)
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                // Ganti URL ini sesuai kebutuhan:
                // - "http://10.0.2.2:3000/" -> Jika pakai Emulator Android & Backend jalan di localhost laptop
                // - "http://192.168.1.x:3000/" -> Jika pakai HP asli (pastikan satu WiFi dengan laptop)
                // - "https://api.domainanda.com/" -> Jika backend sudah online
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}