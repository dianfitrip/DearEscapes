package com.example.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.apiservice.RetrofitClient
import com.example.myapplication.data.model.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var loginStatus by mutableStateOf("")

    fun login(email: String, passInput: String) { // Saya ganti nama param biar tidak bingung

        // 1. Cek input kosong
        if (email.isEmpty() || passInput.isEmpty()) {
            loginStatus = "Gagal: Email dan Password tidak boleh kosong"
            return
        }

        // 2. Cek Panjang Password
        if (passInput.length != 6) { // Pastikan ini sesuai aturan backendmu
            loginStatus = "Gagal: panjang password harus 6"
            return
        }

        viewModelScope.launch {
            try {
                loginStatus = "Loading..."

                // PERBAIKAN DI SINI:
                // Gunakan parameter 'password' sesuai perubahan di LoginRequest tadi
                val request = LoginRequest(email = email, password = passInput)

                val response = RetrofitClient.instance.loginUser(request)

                if (response.isSuccessful) {
                    // response.body() bisa null, gunakan safe call
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        val user = responseBody.data
                        loginStatus = "Berhasil: Selamat datang ${user?.username}"
                    } else {
                        // Backend merespons tapi success = false
                        loginStatus = "Gagal: Email atau Password Salah"
                    }
                } else {
                    // Error HTTP (400, 401, 500)
                    loginStatus = "Gagal: Email atau Password Salah"
                }
            } catch (e: Exception) {
                // Error Jaringan / Server Mati
                loginStatus = "Error: ${e.message}"
            }
        }
    }
}