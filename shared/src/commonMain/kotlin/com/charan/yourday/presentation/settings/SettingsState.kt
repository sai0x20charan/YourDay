package com.charan.yourday.presentation.settings

data class SettingsState(
    val isTodoistConnected: Boolean? = null,
    val weatherUnits: String? = null,
    val appVersion: String? = null,
    val aiModelState: AiModelState = AiModelState(),
    val selectedModelName: String = ""
)

data class AiModelState(
    val isModelDownloaded: Boolean = false,
    val isModelDownloading: Boolean = false,
    val downloadProgress: Float? = null
)
