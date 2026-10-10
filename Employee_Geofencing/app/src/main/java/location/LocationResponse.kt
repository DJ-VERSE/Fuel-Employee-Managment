package com.example.fuelflex.location

data class LocationResponse(
    val message: String,
    val employee_id: String,
    val station: String,
    val distance_meters: Double,
    val geofence_radius_meters: Int,
    val status: String,
    val recorded_at: String
)