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

class LoginViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    var loginStatus by mutableStateOf("")

    fun login(email: String, passInput: String, onSuccess: () -> Unit) {

        // 1. Cek input kosong
        if (email.isEmpty() || passInput.isEmpty()) {
            loginStatus = "Gagal: Email dan Password tidak boleh kosong"
            return
        }

        // 2. Cek Panjang Password
        if (passInput.length != 6) { // Sesuaikan validasi jika perlu (misal min 6)
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
                            userPreferences.saveSession(
                                userId = user.id,
                                username = user.username,
                                email = email // Menambahkan email ke session
                            )

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