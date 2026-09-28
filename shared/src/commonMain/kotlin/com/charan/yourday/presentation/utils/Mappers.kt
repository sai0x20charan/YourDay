package com.charan.yourday.presentation.utils

import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.presentation.home.CurrentWeatherState
import com.charan.yourday.presentation.home.ForecastWeatherState
import com.charan.yourday.presentation.home.TodoDataState
import com.charan.yourday.utils.DateUtils.toMMMDYYYYWithTime
import com.charan.yourday.utils.DateUtils.toTimeString
import com.charan.yourday.utils.WeatherIconName
import com.charan.yourday.utils.WeatherUnitsEnums
import com.charan.yourday.utils.getProviderLogo

fun WeatherData.toCurrentWeatherState(units : WeatherUnitsEnums) : CurrentWeatherState {
    return CurrentWeatherState(
        temp = when(units){
            WeatherUnitsEnums.C -> this.tempC ?: 0
            WeatherUnitsEnums.F -> this.tempF ?: 0
        },
        condition = this.currentCondition ?: "",
        icon = WeatherIconName.weatherIcon(this.temperatureIcon ?:1000, this.isDay == true ),
        location = this.location ?: "",


    )
}

fun List<WeatherData>.toForecastWeatherState(units : WeatherUnitsEnums) : List<ForecastWeatherState> {
    return this.map {
        ForecastWeatherState(
            temp = when(units){
                WeatherUnitsEnums.C -> it.tempC ?: 0
                WeatherUnitsEnums.F -> it.tempF ?: 0
            },
            condition = it.currentCondition ?: "",
            icon = WeatherIconName.weatherIcon(it.temperatureIcon ?:1000, it.isDay == true ),
            time = it.time?.toTimeString()
        )
    }
}

fun List<TodoData>.toTodoDataState() : List<TodoDataState>{
    return this.map {
        TodoDataState(
            id = it.id,
            taskName = it.tasks ?: "",
            taskLink = it.taskLink ?: "",
            isOverDue = it.isOverDue ?: false,
            todoProvider = it.todoProvider ?: "",
            todoImage = it.todoProvider?.getProviderLogo(),
            date = it.date?.toMMMDYYYYWithTime()
        )
    }
}