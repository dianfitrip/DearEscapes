package com.example.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.apiservice.RetrofitClient
import com.example.myapplication.data.model.UpdateProfileRequest
import com.example.myapplication.data.repository.UserPreferences
import kotlinx.coroutines.delay
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
            val username = userPreferences.getUsername.first() ?: ""
            val email = userPreferences.getEmail.first() ?: ""
            uiState = uiState.copy(
                username = username,
                email = email,
                usernameError = null,
                emailError = null,
                passwordError = null
            )
        }
    }

    fun updateUiState(event: EditProfileEvent) {
        uiState = when (event) {
            is EditProfileEvent.UsernameChanged -> {
                uiState.copy(
                    username = event.username,
                    usernameError = validateUsername(event.username)
                )
            }
            is EditProfileEvent.EmailChanged -> {
                uiState.copy(
                    email = event.email,
                    emailError = validateEmail(event.email)
                )
            }
            is EditProfileEvent.PasswordChanged -> {
                uiState.copy(
                    password = event.password,
                    passwordError = validatePassword(event.password)
                )
            }
        }
    }

    // FUNGSI VALIDASI
    internal fun validateUsername(username: String): String? {
        return when {
            username.isBlank() -> "Nama lengkap wajib diisi"
            username.length < 3 -> "Nama lengkap minimal 3 karakter"
            username.length > 50 -> "Nama lengkap maksimal 50 karakter"
            username.any { it.isDigit() } -> "Nama lengkap tidak boleh mengandung angka"
            username.any { !it.isLetter() && !it.isWhitespace() } ->
                "Nama lengkap hanya boleh mengandung huruf dan spasi"
            else -> null
        }
    }

    internal fun validateEmail(email: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return when {
            email.isBlank() -> "Email wajib diisi"
            !email.matches(emailRegex.toRegex()) -> "Format email tidak valid"
            !email.contains("@gmail.com") -> "Email harus menggunakan @gmail.com"
            else -> null
        }
    }

    internal fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> null // Password opsional, jika kosong tidak error
            password.length != 6 -> "Panjang password harus 6 dengan kombinasi huruf dan angka"
            !password.any { it.isDigit() } -> "Password harus mengandung angka"
            !password.any { it.isLetter() } -> "Password harus mengandung huruf"
            else -> null
        }
    }

    // Fungsi untuk cek apakah semua validasi lolos
    private fun isFormValid(): Boolean {
        return validateUsername(uiState.username) == null &&
                validateEmail(uiState.email) == null &&
                validatePassword(uiState.password) == null
    }

    fun saveChanges(navigateBack: () -> Unit) {
        viewModelScope.launch {
            try {
                // Validasi sebelum save
                val usernameError = validateUsername(uiState.username)
                val emailError = validateEmail(uiState.email)
                val passwordError = validatePassword(uiState.password)

                uiState = uiState.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null,
                    usernameError = usernameError,
                    emailError = emailError,
                    passwordError = passwordError
                )

                // Jika ada error validasi, stop proses
                if (usernameError != null || emailError != null || passwordError != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Periksa kembali data yang dimasukkan"
                    )
                    return@launch
                }

                val userId = userPreferences.getUserId.first() ?: run {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Sesi telah berakhir, silakan login kembali"
                    )
                    return@launch
                }

                // Siapkan Request
                val request = UpdateProfileRequest(
                    username = uiState.username,
                    email = uiState.email,
                    password = if (uiState.password.isNotBlank()) uiState.password else null
                )

                // Panggil API
                val response = RetrofitClient.instance.updateUser(userId, request)

                if (response.isSuccessful) {
                    // Anggap sukses jika response code 200-299
                    val isSuccess = response.code() in 200..299

                    if (isSuccess) {
                        // Jika sukses di server, update juga session lokal (DataStore)
                        userPreferences.saveSession(userId, uiState.username, uiState.email)

                        // Reset form dan password setelah berhasil
                        uiState = uiState.copy(
                            isLoading = false,
                            password = "",
                            errorMessage = null,
                            successMessage = "Profil berhasil diperbarui!"
                        )

                        // Tunggu sebentar lalu navigate back
                        delay(1500L)
                        navigateBack()
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = "Gagal update profil. Kode: ${response.code()}"
                        )
                    }
                } else {
                    // Jika response tidak successful
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = if (errorBody != null && errorBody.isNotBlank()) {
                        "Gagal update profil: $errorBody"
                    } else {
                        "Gagal terhubung ke server. Kode: ${response.code()}"
                    }

                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Terjadi kesalahan: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }
}

data class EditProfileState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    // Validasi errors
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)

sealed interface EditProfileEvent {
    data class UsernameChanged(val username: String) : EditProfileEvent
    data class EmailChanged(val email: String) : EditProfileEvent
    data class PasswordChanged(val password: String) : EditProfileEvent
}