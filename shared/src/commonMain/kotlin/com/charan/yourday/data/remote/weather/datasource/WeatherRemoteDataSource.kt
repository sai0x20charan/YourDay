package com.charan.yourday.data.remote.weather.datasource

import com.charan.yourday.data.network.dto.WeatherDTO
import com.charan.yourday.data.network.dto.WeatherError
import com.charan.yourday.data.remote.weather.api.WeatherApi
import com.charan.yourday.utils.ErrorCodes
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import org.koin.core.annotation.Single

@Single
class WeatherRemoteDataSource(
    private val weatherApi: WeatherApi
) {
    suspend fun getCurrentWeather(lat: Double, long: Double): WeatherDTO {
        val response = weatherApi.getCurrentWeather(lat, long)
        return when (response.status) {
            HttpStatusCode.OK -> response.body<WeatherDTO>()
            HttpStatusCode.Unauthorized -> throw Exception(ErrorCodes.UNAUTHORIZED.name)
            else -> {
                val error = runCatching { response.body<WeatherError>() }.getOrNull()
                throw Exception(error?.error?.message ?: "API Error: ${response.status}")
            }
        }
    }

    suspend fun getForecastWeather(lat: Double, long: Double): WeatherDTO {
        val response = weatherApi.getForecastWeather(lat, long)
        return when (response.status) {
            HttpStatusCode.OK -> response.body<WeatherDTO>()
            HttpStatusCode.Unauthorized -> throw Exception(ErrorCodes.UNAUTHORIZED.name)
            else -> {
                val error = runCatching { response.body<WeatherError>() }.getOrNull()
                throw Exception(error?.error?.message ?: "API Error: ${response.status}")
            }
        }
    }
}
