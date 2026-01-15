package com.example.myapplication.ui.viewmodel


import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.apiservice.RetrofitClient
import com.example.myapplication.data.model.RegisterRequest
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    // Hanya menyimpan status Register
    var registerStatus by mutableStateOf("")

    fun register(nama: String, email: String, pass: String) {

        //VALIDASI REGISTER

        // 1. Validasi Nama: Tidak boleh ada angka
        if (nama.any { it.isDigit() }) {
            registerStatus = "Gagal: Nama tidak boleh mengandung angka"
            return
        }

        // 2. Validasi Email: Format email & harus x@gmail.com
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@gmail.com")) {
            registerStatus = "Gagal: Email harus format x@gmail.com"
            return
        }

        // 3. Validasi Password: Harus pas 6 karakter
        if (pass.length != 6) {
            registerStatus = "Gagal: panjang password harus 6"
            return
        }

        //REQUEST API
        viewModelScope.launch {
            try {
                registerStatus = "Loading..."
                val request = RegisterRequest(username = nama, email = email, password = pass)
                val response = RetrofitClient.instance.registerUser(request)

                if (response.isSuccessful) {
                    registerStatus = "Berhasil: ${response.body()?.message}"
                } else {
                    registerStatus = "Gagal: ${response.errorBody()?.string() ?: "Terjadi kesalahan"}"
                }
            } catch (e: Exception) {
                registerStatus = "Error: ${e.message}"
            }
        }
    }
}