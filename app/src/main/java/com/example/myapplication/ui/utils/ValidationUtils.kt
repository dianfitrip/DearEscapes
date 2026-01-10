package com.example.myapplication.ui.utils

import android.util.Patterns

object ValidationUtils {

    // 1. Validasi Nama: Tidak boleh mengandung angka
    fun isNameValid(name: String): Boolean {
        // Regex: Cek apakah string mengandung angka (0-9)
        val hasDigit = name.any { it.isDigit() }
        return name.isNotEmpty() && !hasDigit
    }

    // 2. Validasi Email: Harus format email valid DAN harus berakhiran @gmail.com
    fun isEmailValid(email: String): Boolean {
        return if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email.endsWith("@gmail.com") // Constraint khusus permintaan Anda
        } else {
            false
        }
    }

    // 3. Validasi Password: Minimal 6 karakter (bisa Anda ubah)
    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }
}