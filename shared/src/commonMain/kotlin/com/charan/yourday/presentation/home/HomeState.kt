package com.charan.yourday.presentation.home

import com.charan.yourday.data.model.CalendarItems
import com.charan.yourday.utils.TodoProvidersEnums
import com.charan.yourday.utils.WeatherUnitsEnums
import dev.icerock.moko.resources.ImageResource

data class HomeState(
    val weatherState: WeatherState = WeatherState(),
    val todoState: TodoState = TodoState(),
    val calendarData: CalendarState = CalendarState(),
    val isRefreshing: Boolean = false,
    val showDropDown: Boolean = false,
    val greetings: String = "",
    val currentDateTime: String = ""
) {
    @Deprecated("Use calendarData instead", ReplaceWith("calendarData"))
    val calenderData: CalendarState get() = calendarData
}

data class WeatherState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLocationPermissionGranted: Boolean = true,
    val currentWeather: CurrentWeatherState? = null,
    val forecastWeather: List<ForecastWeatherState> = emptyList(),
    val weatherUnits: String = WeatherUnitsEnums.C.name,
    val scrollToForecastCurrentTimeIndex: Int = 0
)

data class CurrentWeatherState(
    val temp: Int = 0,
    val condition: String = "",
    val icon: ImageResource? = null,
    val location: String = ""
)

data class ForecastWeatherState(
    val time: String? = null,
    val temp: Int = 0,
    val icon: ImageResource? = null,
    val condition: String = ""
)

data class TodoState(
    val isAuthenticating: Boolean = false,
    val isLoading: Boolean = true,
    val todoData: List<TodoDataState>? = null,
    val error: String? = null,
    val isTodoAuthenticated: Boolean = true,
    val todoToken: String? = null,
    val lastSynced: String? = null
) {
    @Deprecated("Use lastSynced instead", ReplaceWith("lastSynced"))
    val lastSycned: String? get() = lastSynced
}

data class TodoDataState(
    val id: String = "",
    val taskName: String = "",
    val taskLink: String = "",
    val isOverDue: Boolean = false,
    val date: String? = null,
    val todoProvider: String = TodoProvidersEnums.TODOIST.name,
    val todoImage: ImageResource? = null
)

data class CalendarState(
    val calendarData: List<CalendarItems>? = null,
    val isLoading: Boolean = false,
    val isCalendarPermissionGranted: Boolean = true,
    val error: String? = null,
    val lastSynced: String? = null
) {
    @Deprecated("Use calendarData instead", ReplaceWith("calendarData"))
    val calenderData: List<CalendarItems>? get() = calendarData
    @Deprecated("Use isCalendarPermissionGranted instead", ReplaceWith("isCalendarPermissionGranted"))
    val isCalenderPermissionGranted: Boolean get() = isCalendarPermissionGranted
    @Deprecated("Use lastSynced instead", ReplaceWith("lastSynced"))
    val lastSycned: String? get() = lastSynced
}

typealias CalenderState = CalendarState
