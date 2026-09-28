package com.charan.yourday.presentation.onBoarding

data class OnBoardingState (
    val isLocationPermissionGranted : Boolean = false,
    val isCalenderPermissionGranted : Boolean = false,
    val isTodoistConnected : Boolean = false,
)