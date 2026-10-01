package com.charan.yourday.data.repository.impl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.charan.yourday.data.model.Location
import com.charan.yourday.data.repository.LocationServiceRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationServiceRepositoryImpl(private val context: Context) : LocationServiceRepository {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }

            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        if (continuation.isActive) {
                            continuation.resume(Location(location.latitude, location.longitude))
                        }
                    } else {
                        try {
                            val priority = if (hasFineLocation) {
                                Priority.PRIORITY_HIGH_ACCURACY
                            } else {
                                Priority.PRIORITY_BALANCED_POWER_ACCURACY
                            }

                            fusedLocationClient.getCurrentLocation(
                                priority,
                                cancellationTokenSource.token
                            ).addOnSuccessListener { newLocation ->
                                if (continuation.isActive) {
                                    if (newLocation != null) {
                                        continuation.resume(
                                            Location(
                                                newLocation.latitude,
                                                newLocation.longitude
                                            )
                                        )
                                    } else {
                                        continuation.resume(null)
                                    }
                                }
                            }.addOnFailureListener {
                                if (continuation.isActive) {
                                    continuation.resume(null)
                                }
                            }
                        } catch (e: Exception) {
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                    }
                }.addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
            } catch (e: Exception) {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }
    }
}
