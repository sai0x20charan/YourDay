package com.charan.yourday.data.repository.impl

import com.charan.yourday.data.model.Location
import com.charan.yourday.data.repository.LocationServiceRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationManager

class LocationServiceRepositoryImpl : LocationServiceRepository {
    private val locationManager = CLLocationManager()

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getCurrentLocation(): Location? {
        return locationManager.location?.coordinate?.useContents {

            Location(latitude, longitude)
        }
    }
}
