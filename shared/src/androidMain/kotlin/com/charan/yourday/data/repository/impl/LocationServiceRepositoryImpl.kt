package com.charan.yourday.data.repository.impl

import android.annotation.SuppressLint
import android.content.Context
import com.charan.yourday.data.model.Location
import com.charan.yourday.data.repository.LocationServiceRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocationServiceRepositoryImpl(private val context: Context) : LocationServiceRepository {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? = suspendCoroutine { continuation ->
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                continuation.resume(Location(location.latitude, location.longitude))
            } else {
                val cancellationTokenSource = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { newLocation ->
                    if (newLocation != null) {
                        continuation.resume(Location(newLocation.latitude, newLocation.longitude))
                    } else {
                        continuation.resume(null)
                    }
                }.addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
            }
        }.addOnFailureListener { e ->
            continuation.resumeWithException(e)
        }
    }
}
