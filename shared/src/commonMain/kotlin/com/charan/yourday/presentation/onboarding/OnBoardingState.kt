package com.charan.yourday.presentation.onboarding

data class OnBoardingState(
    val isLocationPermissionGranted: Boolean = false,
    val isCalendarPermissionGranted: Boolean = false,
    val isTodoistConnected: Boolean = false
) {
    @Deprecated("Use isCalendarPermissionGranted instead", ReplaceWith("isCalendarPermissionGranted"))
    val isCalenderPermissionGranted: Boolean get() = isCalendarPermissionGranted
}
