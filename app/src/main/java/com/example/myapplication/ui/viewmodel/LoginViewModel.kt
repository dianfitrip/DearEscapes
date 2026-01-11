package com.example.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.apiservice.RetrofitClient
import com.example.myapplication.data.model.LoginRequest
import com.example.myapplication.data.repository.UserPreferences
import kotlinx.coroutines.launch

// Constructor menerima UserPreferences
class LoginViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    var loginStatus by mutableStateOf("")

    // Fungsi login menerima callback onSuccess
    fun login(email: String, passInput: String, onSuccess: () -> Unit) {

        // 1. Cek input kosong
        if (email.isEmpty() || passInput.isEmpty()) {
            loginStatus = "Gagal: Email dan Password tidak boleh kosong"
            return
        }

        // 2. Cek Panjang Password
        if (passInput.length != 6) {
            loginStatus = "Gagal: panjang password harus 6"
            return
        }

        viewModelScope.launch {
            try {
                loginStatus = "Loading..."

                val request = LoginRequest(email = email, password = passInput)
                val response = RetrofitClient.instance.loginUser(request)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        val user = responseBody.data

                        if (user != null) {
                            // --- MODIFIKASI: MENYIMPAN ID DAN USERNAME ---
                            // Kita panggil saveSession dengan 2 parameter sesuai update UserPreferences
                            userPreferences.saveSession(user.id, user.username)

                            loginStatus = "Berhasil: Selamat datang ${user.username}"

                            // 3. Panggil callback sukses agar UI pindah halaman
                            onSuccess()
                        }
                    } else {
                        loginStatus = "Gagal: Email atau Password Salah"
                    }
                } else {
                    loginStatus = "Gagal: Email atau Password Salah"
                }
            } catch (e: Exception) {
                loginStatus = "Error: ${e.message}"
            }
        }
    }
}