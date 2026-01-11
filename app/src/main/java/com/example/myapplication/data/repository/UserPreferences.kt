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
        private val USERNAME_KEY = stringPreferencesKey("username")     // Tambahan (Dari Modifikasi)
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token") // Tetap dipertahankan (Dari Reference)
    }

    // 1. Ambil User ID
    val getUserId: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID_KEY]
        }

    // 2. Ambil Username (Tambahan Baru)
    // Default value "User" jika kosong, berguna untuk header Home
    val getUsername: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USERNAME_KEY] ?: "User"
        }

    // 3. Simpan Session (Gabungan)
    // Menerima userId dan username (wajib), serta token (opsional/default kosong)
    suspend fun saveSession(userId: Int, username: String, token: String = "") {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USERNAME_KEY] = username // Simpan Username

            // Simpan Token jika ada (agar logika lama tidak hilang)
            if (token.isNotEmpty()) {
                preferences[USER_TOKEN_KEY] = token
            }
        }
    }

    // 4. Logout (Hapus Data)
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}