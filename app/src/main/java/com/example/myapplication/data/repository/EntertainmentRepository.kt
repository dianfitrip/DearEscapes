package com.example.myapplication.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Any>>(UiState.Loading)
    val loginState: StateFlow<UiState<Any>> = _loginState

    private val _registerState = MutableStateFlow<UiState<Any>>(UiState.Loading)
    val registerState: StateFlow<UiState<Any>> = _registerState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val response = repository.login(email, password)
                if (!response.error) {
                    // Simpan token di sini jika perlu (User Preference)
                    _loginState.value = UiState.Success(response)
                } else {
                    _loginState.value = UiState.Error(response.message)
                }
            } catch (e: HttpException) {
                _loginState.value = UiState.Error(e.message ?: "Terjadi kesalahan server")
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Terjadi kesalahan jaringan")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            try {
                val response = repository.register(name, email, password)
                if (!response.error) {
                    _registerState.value = UiState.Success(response)
                } else {
                    _registerState.value = UiState.Error(response.message)
                }
            } catch (e: Exception) {
                _registerState.value = UiState.Error(e.message ?: "Gagal Register")
            }
        }
    }
}