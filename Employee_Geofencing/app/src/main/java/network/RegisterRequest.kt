package com.example.fuelflex.network

data class RegisterRequest(
    val username: String,
    val password: String,
    val phone: String,
    val employee_id: String
)