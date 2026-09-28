package com.charan.yourday.presentation.onBoarding

sealed interface OnBoardingViewEffect {
    data object OnLocationPermissionRequest : OnBoardingViewEffect
    data object OnCalenderPermissionRequest : OnBoardingViewEffect
}