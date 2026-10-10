package com.example.fuelflex.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest as FusedLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationManager(
    context: Context
) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {

        return suspendCancellableCoroutine { continuation ->

            val request = FusedLocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000
            )
                .setWaitForAccurateLocation(false)
                .setMaxUpdates(1)
                .build()

            fusedLocationClient
                .requestLocationUpdates(
                    request,
                    object : com.google.android.gms.location.LocationCallback() {

                        override fun onLocationResult(
                            result: com.google.android.gms.location.LocationResult
                        ) {
                            fusedLocationClient.removeLocationUpdates(this)

                            continuation.resume(
                                result.lastLocation
                            )
                        }
                    },
                    android.os.Looper.getMainLooper()
                )
        }
    }
}