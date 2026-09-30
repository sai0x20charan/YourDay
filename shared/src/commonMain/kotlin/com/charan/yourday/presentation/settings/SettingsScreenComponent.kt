package com.charan.yourday.presentation.settings

import com.arkivanov.decompose.ComponentContext
import com.charan.yourday.data.repository.LocalLLMRepository
import com.charan.yourday.data.repository.TodoistRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.utils.appVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SettingsScreenComponent(
    componentContext: ComponentContext,
    val onBackClick: () -> Unit,
    val onLicenseClick: () -> Unit = {},
) : KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val userPreferencesRepository: UserPreferencesRepository = get()
    private val todoistRepository: TodoistRepository = get()
    private val localLLMRepository: LocalLLMRepository = get()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()

    init {
        getSetTemperatureUnits()
        isTodoConnected()
        getAppVersion()
        observeSelectedModel()
    }

    private fun getSetTemperatureUnits() = coroutineScope.launch {
        userPreferencesRepository.weatherUnits.collectLatest {
            updateSettingsState(
                weatherUnits = it
            )
        }
    }

    private fun getAppVersion() {
        updateSettingsState(
            appVersion = appVersion()
        )
    }

    private fun setTemperature(weatherUnits: String) = coroutineScope.launch {
        userPreferencesRepository.setWeatherUnits(weatherUnits)
    }

    private fun deleteTodoistToken() = coroutineScope.launch {
        userPreferencesRepository.clearTodoistAccessToken()
    }

    private fun isTodoConnected() = coroutineScope.launch {
        userPreferencesRepository.todoistAccessToken.collectLatest {
            if (it != null) {
                updateSettingsState(
                    isTodoistConnected = true
                )
            } else {
                updateSettingsState(
                    isTodoistConnected = false
                )
            }
        }
    }

    private fun observeSelectedModel() = coroutineScope.launch {
        localLLMRepository.selectedModel.collectLatest { selectedModel ->
            val isDownloaded = runCatching {
                localLLMRepository.isModelDownloaded()
            }.getOrDefault(false)
            _settingsState.update {
                it.copy(
                    selectedModelName = selectedModel.name,
                    aiModelState = it.aiModelState.copy(
                        isModelDownloaded = isDownloaded
                    )
                )
            }
        }
    }

    private fun downloadAIModel() = coroutineScope.launch {
        localLLMRepository.downloadModel().collectLatest { status ->
            when {
                status.isFailed -> {
                    _settingsState.update {
                        it.copy(
                            aiModelState = it.aiModelState.copy(
                                isModelDownloading = false,
                                downloadProgress = null
                            )
                        )
                    }
                }

                status.isDownloaded -> {
                    _settingsState.update {
                        it.copy(
                            aiModelState = it.aiModelState.copy(
                                isModelDownloading = false,
                                isModelDownloaded = true,
                                downloadProgress = null
                            )
                        )
                    }
                }

                else -> {
                    _settingsState.update {
                        it.copy(
                            aiModelState = it.aiModelState.copy(
                                isModelDownloading = true,
                                downloadProgress = status.progress
                            )
                        )
                    }
                }
            }
        }
    }

    private fun deleteAIModel() = coroutineScope.launch {
        localLLMRepository.deleteModel()
            .onSuccess {
                _settingsState.update {
                    it.copy(
                        aiModelState = it.aiModelState.copy(
                            isModelDownloaded = false
                        )
                    )
                }
            }
    }

    fun onEvent(event: SettingsEvents) = coroutineScope.launch {
        when (event) {
            is SettingsEvents.OnChangeWeatherUnits -> {
                setTemperature(event.weatherUnit)
            }

            SettingsEvents.TodoConnect -> {
                if (_settingsState.value.isTodoistConnected == false) {
                    todoistRepository.requestAuthorization()
                } else {
                    deleteTodoistToken()
                }
            }

            SettingsEvents.onBack -> {
                onBackClick()
            }

            SettingsEvents.OnLicenseNavigate -> {
                onLicenseClick()
            }

            SettingsEvents.OnDownloadAIModel -> {
                downloadAIModel()
            }

            SettingsEvents.OnDeleteAIModel -> {
                deleteAIModel()
            }
        }
    }

    private fun updateSettingsState(
        weatherUnits: String? = null,
        isTodoistConnected: Boolean? = null,
        appVersion: String? = null
    ) {
        _settingsState.update {
            it.copy(
                weatherUnits = weatherUnits ?: it.weatherUnits,
                isTodoistConnected = isTodoistConnected ?: it.isTodoistConnected,
                appVersion = appVersion ?: it.appVersion
            )
        }
    }
}
