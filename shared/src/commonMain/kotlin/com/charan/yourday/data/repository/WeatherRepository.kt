package com.charan.yourday.data.repository

import com.charan.yourday.data.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    val weatherData: Flow<WeatherData?>

    suspend fun refreshWeather(lat: Double, long: Double): Result<WeatherData>
}
