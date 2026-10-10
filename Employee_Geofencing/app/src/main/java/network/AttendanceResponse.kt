package com.example.fuelflex.network

data class AttendanceResponse(
    val message: String,
    val employee_id: String? = null,
    val date: String? = null,
    val check_in: String? = null,
    val check_out: String? = null,
    val status: String? = null,
    val error: String? = null
)