package com.example.vero.api

import com.example.vero.api.dtos.AuthRequest
import com.example.vero.api.dtos.AuthResponse
import com.example.vero.api.dtos.TaskDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TaskApiService {
    @POST("index.php/login")
    suspend fun login(
        @Header("Authorization") authorization: String = "Basic QVBJX0V4cGxvcmVyOjEyMzQ1NmlzQUxhbWVQYXNz",
        @Header("Content-Type") contentType: String = "application/json",
        @Body authRequest: AuthRequest
    ): AuthResponse

    @GET("dev/index.php/v1/tasks/select")
    suspend fun getTasks(): List<TaskDto>
}