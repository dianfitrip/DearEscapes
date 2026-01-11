package com.example.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.apiservice.RetrofitClient
import com.example.myapplication.data.model.UpdateProfileRequest
import com.example.myapplication.data.repository.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    var uiState by mutableStateOf(EditProfileState())
        private set

    init {
        loadCurrentData()
    }

    private fun loadCurrentData() {
        viewModelScope.launch {
            val username = userPreferences.getUsername.first()
            val email = userPreferences.getEmail.first()
            uiState = uiState.copy(username = username, email = email)
        }
    }

    fun updateUiState(event: EditProfileEvent) {
        uiState = when (event) {
            is EditProfileEvent.UsernameChanged -> uiState.copy(username = event.username)
            is EditProfileEvent.EmailChanged -> uiState.copy(email = event.email)
            is EditProfileEvent.PasswordChanged -> uiState.copy(password = event.password)
        }
    }

    fun saveChanges(navigateBack: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            // Validasi Sederhana
            if (uiState.username.isBlank() || uiState.email.isBlank()) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Username dan Email wajib diisi")
                return@launch
            }

            try {
                val userId = userPreferences.getUserId.first() ?: return@launch

                // Siapkan Request
                val request = UpdateProfileRequest(
                    username = uiState.username,
                    email = uiState.email,
                    password = if (uiState.password.isNotBlank()) uiState.password else null
                )

                // Panggil API
                val response = RetrofitClient.instance.updateUser(userId, request)

                if (response.isSuccessful) {
                    // Jika sukses di server, update juga session lokal (DataStore)
                    userPreferences.saveSession(userId, uiState.username, uiState.email)
                    navigateBack()
                } else {
                    uiState = uiState.copy(errorMessage = "Gagal update: ${response.message()}")
                }

            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Error: ${e.message}")
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}

data class EditProfileState(
    val username: String = "",
    val email: String = "",
    val password: String = "", // Kosongkan defaultnya
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface EditProfileEvent {
    data class UsernameChanged(val username: String) : EditProfileEvent
    data class EmailChanged(val email: String) : EditProfileEvent
    data class PasswordChanged(val password: String) : EditProfileEvent
}