package com.charan.yourday.presentation.home

import com.charan.yourday.data.model.CalenderItems
import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.model.TodoProvider
import com.charan.yourday.utils.TodoProvidersEnums
import com.charan.yourday.utils.WeatherUnitsEnums
import dev.icerock.moko.resources.ImageResource
import kotlinx.datetime.LocalDateTime


data class HomeState(
    val weatherState: WeatherState = WeatherState(),
    val todoState: TodoState = TodoState(),
    val calenderData: CalenderState = CalenderState(),
    val isRefreshing : Boolean = false
)

data class WeatherState(
    val isLoading : Boolean = false,
    val error : String? = null,
    val isLocationPermissionGranted : Boolean = false,
    val currentWeather: CurrentWeatherState? = null,
    val forecastWeather : List<ForecastWeatherState> = emptyList(),
    val weatherUnits : String = WeatherUnitsEnums.C.name
)

data class CurrentWeatherState(
    val temp : Int =0,
    val condition : String ="",
    val icon : ImageResource? = null,
    val location : String = ""
)

data class ForecastWeatherState(
    val time : String? = null,
    val temp : Int = 0,
    val icon: ImageResource ? = null,
    val condition : String = ""
)

data class TodoState(
    val isAuthenticating : Boolean = false,
    val isLoading : Boolean = true,
    val todoData : List<TodoDataState>? = null,
    val error : String? = null,
    val isTodoAuthenticated: Boolean = false,
    val todoToken : String? = null,
    val lastSycned : String? = null
)

data class TodoDataState(
    val id : String = "",
    val taskName : String = "",
    val taskLink : String = "",
    val isOverDue : Boolean = false,
    val date : String? = null,
    val todoProvider: String = TodoProvidersEnums.TODOIST.name,
    val todoImage : ImageResource? = null
)

data class CalenderState(
    val calenderData : List<CalenderItems>? = null,
    val isLoading: Boolean = false,
    val isCalenderPermissionGranted : Boolean = false,
    val error : String? = null,
    val lastSycned: String? = null
)


