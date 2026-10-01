package com.charan.yourday.presentation.settings


data class SettingsState(
    val isTodoistConnected : Boolean? = null,
    val weatherUnits : String? = null,
    val appVersion : String? =null
)