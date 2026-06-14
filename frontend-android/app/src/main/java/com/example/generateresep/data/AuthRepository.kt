package com.example.generateresep.data

import com.example.generateresep.data.api.AuthApiService
import com.example.generateresep.model.LoginRequest
import com.example.generateresep.model.LoginResponse
import com.example.generateresep.model.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService
) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiService.login(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<LoginResponse> {
        return try {
            val response = apiService.register(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
