package com.charan.yourday.data.repository

import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.utils.WeatherUnitsEnums
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val weatherUnit : Flow<WeatherUnitsEnums>

    suspend fun setWeatherUnit(unit : WeatherUnitsEnums)

    val todoistAccessToken : Flow<String?>

    suspend fun setTodoistAccessToken(token : String)

    val shouldShowOnboarding : Flow<Boolean>

    suspend fun setShouldShowOnboarding(shouldShow : Boolean)

    val weatherData : Flow<WeatherData>
    suspend fun setWeatherData(weatherData: WeatherData)

    val todoData : Flow<List<TodoData>>

    suspend fun setTodoData(todoData: List<TodoData>)


}