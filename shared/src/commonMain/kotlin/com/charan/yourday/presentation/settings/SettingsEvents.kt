package com.charan.yourday.presentation.settings

sealed class SettingsEvents {
        data class OnChangeWeatherUnits(val weatherUnit : String) : SettingsEvents()
        data object TodoConnect : SettingsEvents()
        data object onBack : SettingsEvents()
        data object OnLicenseNavigate : SettingsEvents()
}