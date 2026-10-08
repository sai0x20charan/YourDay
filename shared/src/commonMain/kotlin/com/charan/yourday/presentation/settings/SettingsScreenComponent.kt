package com.charan.yourday.presentation.settings

import com.arkivanov.decompose.ComponentContext
import com.charan.yourday.data.repository.TodoistRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.presentation.common.DropDownItem
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

class SettingsScreenComponent(
    componentContext: ComponentContext,
    val onBackClick: () -> Unit,
    val onLicenseClick: () -> Unit = {},
) : KoinComponent, ComponentContext by componentContext {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val userPreferencesRepository: UserPreferencesRepository = get()
    private val todoistRepository: TodoistRepository = get()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()

    init {
        getSetTemperatureUnits()
        isTodoConnected()
        getAppVersion()
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

    private fun updateDropdownMenuState(show: Boolean) {
        _settingsState.update {
            it.copy(
                showDropDown = show
            )
        }
    }

    fun onEvent(event: SettingsEvents) = coroutineScope.launch {
        when (event) {
            is SettingsEvents.OnChangeWeatherUnits -> {
                setTemperature(event.weatherUnit)
            }

            is SettingsEvents.ShowDropdownMenu -> {
                updateDropdownMenuState(event.show)
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
        }
    }

    private fun updateSettingsState(
        weatherUnits: String? = null,
        isTodoistConnected: Boolean? = null,
        appVersion: String? = null,
        dropDownItems: List<DropDownItem>? = null
    ) {
        _settingsState.update {
            it.copy(
                weatherUnits = weatherUnits ?: it.weatherUnits,
                isTodoistConnected = isTodoistConnected ?: it.isTodoistConnected,
                appVersion = appVersion ?: it.appVersion,
                dropDownItems = dropDownItems ?: it.dropDownItems
            )
        }
    }
}
