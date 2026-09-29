package com.charan.yourday.data.repository

import com.charan.yourday.data.model.Location

interface LocationServiceRepository {
    suspend fun getCurrentLocation(): Location?
}
