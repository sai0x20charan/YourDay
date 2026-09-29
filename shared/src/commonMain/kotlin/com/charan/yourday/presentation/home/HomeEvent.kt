package com.charan.yourday.presentation.home

sealed interface HomeEvent {
    data object RequestLocationPermission : HomeEvent
    data object RequestCalendarPermission : HomeEvent
    object ConnectTodoist : HomeEvent
    object FetchWeather : HomeEvent
    object FetchCalendarEvents : HomeEvent
    object DisconnectTodoist : HomeEvent
    object FetchTodo : HomeEvent
    object OpenSettingsPage : HomeEvent
    data class OnOpenLink(val url: String) : HomeEvent
    object RefreshData : HomeEvent
    data class ShowDropdownMenu(val show: Boolean) : HomeEvent
}
