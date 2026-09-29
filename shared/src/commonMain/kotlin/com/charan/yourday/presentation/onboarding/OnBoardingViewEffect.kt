package com.charan.yourday.presentation.onboarding

sealed interface OnBoardingViewEffect {
    data object OnLocationPermissionRequest : OnBoardingViewEffect
    data object OnCalenderPermissionRequest : OnBoardingViewEffect
}
