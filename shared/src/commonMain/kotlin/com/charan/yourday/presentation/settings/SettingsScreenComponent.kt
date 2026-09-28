package com.charan.yourday.presentation.settings

import com.arkivanov.decompose.ComponentContext
import com.charan.yourday.data.repository.LocalLLMRepository
import com.charan.yourday.data.repository.TodoistRepo
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.UserPreferencesStore
import com.charan.yourday.utils.appVersion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SettingsScreenComponent (
    componentContext: ComponentContext,
    val onBackClick : () -> Unit,
    val onLicenseClick : () -> Unit ={},

) : KoinComponent, ComponentContext by componentContext{
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val userPreferences : UserPreferencesStore = get()
    private val todoistRepo: TodoistRepo = get()
    private val localLLMRepository : LocalLLMRepository = get()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()
    init {
        getSetTemperatureUnits()
        isTodoConnected()
        getAppVersion()
        isAIModelDownloaded()
    }

    private fun getSetTemperatureUnits() = coroutineScope.launch{
        userPreferences.weatherUnits.collectLatest {
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
    private fun setTemperature(weatherUnits : String) = coroutineScope.launch {
       userPreferences.setWeatherUnits(weatherUnits)
    }
    private fun deleteTodoistToken() = coroutineScope.launch {
        userPreferences.deleteTodoistToken()
    }
    private fun isTodoConnected() = coroutineScope.launch {
        userPreferences.todoistAccessToken.collectLatest {
            if(it !=null){
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

    private fun downloadAIModel() = coroutineScope.launch {
        localLLMRepository.downloadModel().collectLatest { processState ->
            when(processState){
                is ProcessState.Loading -> {
                    _settingsState.update {
                        it.copy(
                            aiModelState = it.aiModelState.copy(
                                downloadProgress = processState.progress
                            )
                        )
                    }
                }
                is ProcessState.Success -> {
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

                is ProcessState.Error -> {


                }

                else -> {}
            }
        }
    }

    private fun deleteAIModel() = coroutineScope.launch {


    }


    fun onEvent(event : SettingsEvents) = coroutineScope.launch{
        when(event){
            is SettingsEvents.OnChangeWeatherUnits -> {
                setTemperature(event.weatherUnit)
            }

            SettingsEvents.TodoConnect -> {
                if(_settingsState.value.isTodoistConnected==false){
                    todoistRepo.requestAuthorization()
                } else{
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
            else -> {}
        }
    }

    private fun updateSettingsState(
        weatherUnits: String? =null,
        isTodoistConnected : Boolean? =null,
        appVersion : String?=null
    ) {
        _settingsState.update {
            it.copy(
                weatherUnits = weatherUnits ?: it.weatherUnits,
                isTodoistConnected =  isTodoistConnected ?: it.isTodoistConnected,
                appVersion = appVersion ?: it.appVersion
            )

        }
    }

    private fun isAIModelDownloaded() = coroutineScope.launch {
        val isDownloaded = localLLMRepository.isModelDownloaded()
        _settingsState.update {
            it.copy(
                aiModelState = it.aiModelState.copy(
                    isModelDownloaded = isDownloaded
                )
            )
        }


    }
}