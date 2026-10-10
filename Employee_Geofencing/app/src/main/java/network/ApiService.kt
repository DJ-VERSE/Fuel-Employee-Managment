package com.example.fuelflex.network

import com.example.fuelflex.location.LocationRequest
import com.example.fuelflex.location.LocationResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("No-Auth: true")
    @POST("api/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @Headers("No-Auth: true")
    @POST("api/register/")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("api/locations/")
    suspend fun sendLocation(
        @Body request: LocationRequest
    ): LocationResponse
    @POST("api/attendance/check-in/")
    suspend fun checkIn(): AttendanceResponse

    @POST("api/attendance/check-out/")
    suspend fun checkOut(): AttendanceResponse
}