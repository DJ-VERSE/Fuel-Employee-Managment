package com.example.fuelflex.location

data class LocationRequest(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float?
)