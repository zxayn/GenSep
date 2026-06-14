package com.example.generateresep.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.generateresep.data.AuthRepository
import com.example.generateresep.model.LoginRequest
import com.example.generateresep.model.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isAuthSuccess by mutableStateOf(false)

    fun login() {
        // Bypass untuk testing
        isAuthSuccess = true
        /*
        if (username.isBlank() || password.isBlank()) return
        
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.login(LoginRequest(username, password))
            result.onSuccess {
                if (it.token != null) {
                    isAuthSuccess = true
                } else {
                    errorMessage = it.error ?: "Login Gagal"
                }
            }.onFailure {
                errorMessage = "Gagal terhubung ke server"
            }
            isLoading = false
        }
        */
    }

    fun register() {
        // Bypass untuk testing
        isAuthSuccess = true
        /*
        if (username.isBlank() || email.isBlank() || password.isBlank()) return
        
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.register(RegisterRequest(username, email, password))
            result.onSuccess {
                if (it.token != null) {
                    isAuthSuccess = true
                } else {
                    errorMessage = it.error ?: "Registrasi Gagal"
                }
            }.onFailure {
                errorMessage = "Gagal terhubung ke server"
            }
            isLoading = false
        }
        */
    }
}
