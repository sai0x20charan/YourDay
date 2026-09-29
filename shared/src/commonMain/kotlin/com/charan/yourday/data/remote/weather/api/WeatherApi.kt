package com.charan.yourday.data.remote.weather.api

import com.charan.yourday.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import org.koin.core.annotation.Single

@Single
class WeatherApi(
    private val client: HttpClient
) {
    companion object {
        private const val WEATHER_BASE_URL = "api.weatherapi.com"
    }

    suspend fun getCurrentWeather(lat: Double, long: Double): HttpResponse {
        return client.get {
            url {
                host = WEATHER_BASE_URL
                path("current.json")
                parameters.append("q", "$lat,$long")
                parameters.append("key", BuildKonfig.API_KEY)
            }
        }
    }

    suspend fun getForecastWeather(lat: Double, long: Double): HttpResponse {
        return client.get {
            url {
                host = WEATHER_BASE_URL
                path("/v1/forecast.json")
                parameters.append("q", "$lat,$long")
                parameters.append("key", BuildKonfig.API_KEY)
            }
        }
    }
}
