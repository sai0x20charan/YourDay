package com.charan.yourday.presentation.home

sealed interface HomeEvent {
    data class RequestLocationPermission(val showRationale: Boolean) : HomeEvent
    data class RequestCalendarPermission(val showRationale: Boolean) : HomeEvent
    object ConnectTodoist : HomeEvent
    object FetchWeather : HomeEvent
    object FetchCalendarEvents : HomeEvent
    object FetchTodo : HomeEvent
    object DisconnectTodoist : HomeEvent
    object OpenSettingsPage : HomeEvent
    data class OnOpenLink(val url : String) : HomeEvent
    object RefreshData : HomeEvent
    object OnBoardingFinish : HomeEvent
}