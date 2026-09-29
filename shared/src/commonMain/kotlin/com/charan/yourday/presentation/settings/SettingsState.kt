package com.charan.yourday.presentation.settings

import com.charan.yourday.utils.WeatherUnits

data class SettingsState(
    val isTodoistConnected : Boolean? = null,
    val weatherUnits : String? = null,
    val appVersion : String? = null,
    val aiModelState : AiModelState = AiModelState()
)

data class AiModelState(
    val isModelDownloaded : Boolean = false,
    val isModelDownloading : Boolean = false,
    val downloadProgress : Float? = null
)
