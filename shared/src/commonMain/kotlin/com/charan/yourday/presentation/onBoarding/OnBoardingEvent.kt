package com.charan.yourday.presentation.onBoarding

sealed interface OnBoardingEvent {

    data class onLocationPermissionGrant(val shouldShowRationale: Boolean): OnBoardingEvent
    data class onCalenderPermissionGrant(val shouldShowRationale: Boolean): OnBoardingEvent
    object onTodoistConnect: OnBoardingEvent

}
