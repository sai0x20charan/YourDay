package com.charan.yourday.presentation.onboarding

sealed interface OnBoardingEvent {
    data object RequestLocationPermission : OnBoardingEvent
    data object RequestCalendarPermission : OnBoardingEvent
    data object ConnectTodoist : OnBoardingEvent
    data object Finish : OnBoardingEvent
    data object CheckPermissions : OnBoardingEvent

    @Deprecated("Use RequestLocationPermission", ReplaceWith("RequestLocationPermission"))
    data class onLocationPermissionGrant(val shouldShowRationale: Boolean = false) : OnBoardingEvent
    @Deprecated("Use RequestCalendarPermission", ReplaceWith("RequestCalendarPermission"))
    data class onCalenderPermissionGrant(val shouldShowRationale: Boolean = false) : OnBoardingEvent
    @Deprecated("Use ConnectTodoist", ReplaceWith("ConnectTodoist"))
    data object onTodoistConnect : OnBoardingEvent
}
