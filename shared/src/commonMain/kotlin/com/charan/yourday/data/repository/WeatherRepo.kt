package com.charan.yourday.data.repository

import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface WeatherRepo {
    suspend fun getCurrentWeatherInfo(lat : Double, long: Double) : Flow<ProcessState<WeatherDTO?>>
    suspend fun getCurrentForecast(lat : Double, long: Double) : Flow<ProcessState<Boolean>>
}