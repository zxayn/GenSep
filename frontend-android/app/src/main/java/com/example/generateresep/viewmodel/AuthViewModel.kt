package com.example.generateresep.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.generateresep.data.AuthRepository
import com.example.generateresep.data.TokenManager
import com.example.generateresep.model.LoginRequest
import com.example.generateresep.model.RegisterRequest
import com.example.generateresep.utils.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isAuthSuccess by mutableStateOf(false)

    fun login() {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Username dan Password tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.login(LoginRequest(username, password))
            result.onSuccess {
                if (it.token != null) {
                    tokenManager.saveToken(it.token, it.username ?: username)
                    isAuthSuccess = true
                    UserSession.currentUsername = it.username ?: username
                } else {
                    errorMessage = it.error ?: "Login Gagal"
                }
            }.onFailure {
                errorMessage = "Gagal terhubung ke server. Pastikan IP server benar."
            }
            isLoading = false
        }
    }

    fun register() {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            errorMessage = "Semua kolom harus diisi"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.register(RegisterRequest(username, email, password))
            result.onSuccess {
                if (it.token != null) {
                    tokenManager.saveToken(it.token, it.username ?: username)
                    isAuthSuccess = true
                    UserSession.currentUsername = it.username ?: username
                } else {
                    errorMessage = it.error ?: "Registrasi Gagal"
                }
            }.onFailure {
                errorMessage = "Gagal terhubung ke server. Pastikan IP server benar."
            }
            isLoading = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            UserSession.clearSession()
            isAuthSuccess = false
            username = ""
            password = ""
            email = ""
        }
    }
}
