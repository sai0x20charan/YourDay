package com.charan.yourday.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.charan.yourday.data.mapper.toWeatherData
import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.data.remote.weather.datasource.WeatherRemoteDataSource
import com.charan.yourday.data.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WeatherRepositoryImpl(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val dataStore: DataStore<Preferences>
) : WeatherRepository {

    companion object {
        private val WEATHER_DATA_KEY = stringPreferencesKey("weather_data")
        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    override val weatherData: Flow<WeatherData?> = dataStore.data.map { preferences ->
        preferences[WEATHER_DATA_KEY]?.let { jsonString ->
            runCatching {
                json.decodeFromString<WeatherData>(jsonString)
            }.getOrNull()
        }
    }

    override suspend fun refreshWeather(lat: Double, long: Double): Result<WeatherData> {
        return runCatching {
            val weatherDTO = weatherRemoteDataSource.getForecastWeather(lat, long)
            val mapped = weatherDTO.toWeatherData()
            dataStore.edit { preferences ->
                preferences[WEATHER_DATA_KEY] = json.encodeToString(mapped)
            }
            mapped
        }
    }
}
