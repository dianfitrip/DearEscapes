package com.example.myapplication.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token") // Tetap ada (dari kode lama)
        private val EMAIL_KEY = stringPreferencesKey("email")           // Tambahan (dari modifikasi)
    }

    // 1. Ambil User ID
    val getUserId: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID_KEY]
        }

    // 2. Ambil Username
    val getUsername: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USERNAME_KEY] ?: "User"
        }

    // 3. Ambil Email (Fitur Baru)
    val getEmail: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[EMAIL_KEY] ?: "user@example.com" // Default value jika kosong
        }

    // 4. Simpan Session (Gabungan)
    // Sekarang WAJIB menerima email. Token tetap opsional agar fleksibel.
    suspend fun saveSession(userId: Int, username: String, email: String, token: String = "") {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USERNAME_KEY] = username
            preferences[EMAIL_KEY] = email // Simpan Email

            // Simpan Token jika ada
            if (token.isNotEmpty()) {
                preferences[USER_TOKEN_KEY] = token
            }
        }
    }

    // 5. Logout (Hapus Data)
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}