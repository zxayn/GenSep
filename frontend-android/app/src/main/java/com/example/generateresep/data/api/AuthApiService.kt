package com.example.generateresep.data.api

import com.example.generateresep.model.LoginRequest
import com.example.generateresep.model.LoginResponse
import com.example.generateresep.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/register/")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    @POST("api/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
