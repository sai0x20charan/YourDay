package com.charan.yourday.data.repository

import com.charan.yourday.utils.WeatherUnitsEnums
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val weatherUnits: Flow<String>
    val weatherUnitEnum: Flow<WeatherUnitsEnums>
    val todoistAccessToken: Flow<String?>
    val shouldShowOnboarding: Flow<Boolean>

    suspend fun setWeatherUnits(weatherUnits: String)
    suspend fun setWeatherUnit(weatherUnit: WeatherUnitsEnums)
    suspend fun setTodoistAccessToken(token: String)
    suspend fun clearTodoistAccessToken()
    suspend fun setShouldShowOnboarding(shouldShow: Boolean)
}
