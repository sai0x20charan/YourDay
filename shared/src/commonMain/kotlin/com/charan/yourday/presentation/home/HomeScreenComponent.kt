package com.charan.yourday.presentation.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import com.charan.yourday.data.model.LlmGenerationEvent
import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.data.repository.CalendarEventsRepository
import com.charan.yourday.data.repository.LocalLLMRepository
import com.charan.yourday.data.repository.LocationServiceRepository
import com.charan.yourday.data.repository.TodoistRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.data.repository.WeatherRepository
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.presentation.toCurrentWeatherState
import com.charan.yourday.presentation.toForecastWeatherState
import com.charan.yourday.presentation.toTodoDataState
import com.charan.yourday.presentation.utils.SummaryPromptBuilder.generateSummaryPrompt
import com.charan.yourday.utils.DateUtils
import com.charan.yourday.utils.DateUtils.getGreeting
import com.charan.yourday.utils.DateUtils.toDDMMYYYY
import com.charan.yourday.utils.ErrorCodes
import com.charan.yourday.utils.OpenURL
import com.splendo.kaluga.permissions.base.PermissionState
import com.splendo.kaluga.permissions.base.Permissions
import com.splendo.kaluga.permissions.base.PermissionsBuilder
import com.splendo.kaluga.permissions.calendar.CalendarPermission
import com.splendo.kaluga.permissions.location.LocationPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class HomeScreenComponent(
    val authorizationId: String?,
    private val errorCode: String?,
    private val onSettingsOpen: () -> Unit = {},
    componentContext: ComponentContext
) : KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeEffect>()
    val effects = _effects.asSharedFlow()

    private val weatherRepository: WeatherRepository = get()
    private val locationServiceRepository: LocationServiceRepository = get()
    private val permissionManager: PermissionManager = get()
    private val calendarEventsRepository: CalendarEventsRepository = get()
    private val todoistRepository: TodoistRepository = get()
    private val userPreferencesRepository: UserPreferencesRepository = get()
    private val localLLMRepo: LocalLLMRepository = get()

    private val permissionsBuilder: PermissionsBuilder = get()
    private val permissions = Permissions(permissionsBuilder)
    private val locationPermission = LocationPermission(background = false, precise = true)
    private val calendarPermission = CalendarPermission()

    // Cached permission states
    private val _isLocationPermissionGranted = MutableStateFlow(false)
    private val _isCalendarPermissionGranted = MutableStateFlow(false)

    init {
        observeLocationPermission()
        observeCalendarPermission()
        observeWeatherData()
        observeTodoData()
        observeCachedSummary()
        coroutineScope.launch {
            authorizationId?.let { handleAuthorizationCode(it) }
            errorCode?.let { sendEffect(HomeEffect.ShowToast("Unable to authenticate")) }
        }
        refreshData()
        lifecycle.subscribe(
            onResume = {
                if (_state.value.aiResponseState.isModelDownloaded) {
                    //generateSummary()
                }
            }
        )
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.RequestLocationPermission -> handleLocationPermission()
            is HomeEvent.RequestCalendarPermission -> handleCalendarPermission()
            HomeEvent.ConnectTodoist -> requestTodoistAuthentication()
            HomeEvent.FetchWeather -> fetchLocationAndWeather()
            HomeEvent.FetchCalendarEvents -> fetchCalendarEvents()
            HomeEvent.DisconnectTodoist -> disconnectTodoist()
            HomeEvent.OpenSettingsPage -> {
                updateDropdownMenuState(false)
                onSettingsOpen()
            }
            is HomeEvent.OnOpenLink -> openURL(event.url)
            HomeEvent.FetchTodo -> refreshTodoTasks()
            HomeEvent.RefreshData -> refreshData()
            is HomeEvent.ShowDropdownMenu -> updateDropdownMenuState(event.show)
            HomeEvent.OnGenerateAIResponse -> generateSummary(forceRefresh = true)
            HomeEvent.OnToggleThinkingResponse -> {
                _state.update {
                    it.copy(
                        aiResponseState = it.aiResponseState.copy(
                            showThinkingResponse = !it.aiResponseState.showThinkingResponse
                        )
                    )
                }
            }
        }
    }

    private fun updateDropdownMenuState(show: Boolean) {
        _state.update { it.copy(showDropDown = show) }
    }

    private fun refreshData() {
        refreshDateAndGreetings()
        fetchLocationAndWeather()
        fetchCalendarEvents()
        refreshTodoTasks()
        updateDropdownMenuState(false)
    }

    private fun refreshDateAndGreetings() {
        val localDateTime = DateUtils.getCurrentDateTime()
        _state.update {
            it.copy(
                greetings = localDateTime.getGreeting(),
                currentDateTime = localDateTime.toDDMMYYYY()
            )
        }
    }

    private fun observeLocationPermission() = coroutineScope.launch {
        permissions[locationPermission].filter { it !is PermissionState.Uninitialized }.collectLatest { permissionState ->
            val isGranted = permissionState is PermissionState.Allowed
            _isLocationPermissionGranted.value = isGranted
            _state.update {
                it.copy(weatherState = it.weatherState.copy(isLocationPermissionGranted = isGranted))
            }
            if (isGranted) {
                fetchLocationAndWeather()
            }
        }
    }

    private fun observeCalendarPermission() = coroutineScope.launch {
        permissions[calendarPermission].filter { it !is PermissionState.Uninitialized }.collectLatest { permissionState ->
            val isGranted = permissionState is PermissionState.Allowed
            _isCalendarPermissionGranted.value = isGranted
            _state.update {
                it.copy(calendarData = it.calendarData.copy(isCalendarPermissionGranted = isGranted))
            }
            if (isGranted) {
                fetchCalendarEvents()
            }
        }
    }

    private fun handleLocationPermission() = coroutineScope.launch {
        when (permissions[locationPermission].peekState()) {
            is PermissionState.Allowed -> fetchLocationAndWeather()
            is PermissionState.Denied.Requestable,
            is PermissionState.Uninitialized -> permissions.request(locationPermission)
            is PermissionState.Denied.Locked -> permissionManager.openAppSettings()
            else -> {}
        }
    }

    private fun handleCalendarPermission() = coroutineScope.launch {
        when (permissions[calendarPermission].peekState()) {
            is PermissionState.Allowed -> fetchCalendarEvents()
            is PermissionState.Denied.Requestable,
            is PermissionState.Uninitialized -> permissions.request(calendarPermission)
            is PermissionState.Denied.Locked -> permissionManager.openAppSettings()
            else -> {}
        }
    }

    private fun fetchLocationAndWeather() = coroutineScope.launch {
        _state.update {
            it.copy(
                weatherState = it.weatherState.copy(
                    isLoading = true,
                    error = null
                )
            )
        }
        val location = locationServiceRepository.getCurrentLocation()
        if (location != null) {
            val lat = location.latitude ?: 0.0
            val long = location.longitude ?: 0.0
            weatherRepository.refreshWeather(lat, long)
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            weatherState = it.weatherState.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to fetch weather"
                            )
                        )
                    }
                }
                .onSuccess {
                    _state.update {
                        it.copy(
                            weatherState = it.weatherState.copy(
                                isLoading = false,
                                error = null
                            )
                        )
                    }
                }
        } else {
            _state.update {
                it.copy(
                    weatherState = it.weatherState.copy(
                        isLoading = false,
                        error = "Unable to fetch location"
                    )
                )
            }
        }
    }

    private fun openURL(url: String) {
        OpenURL.openURL(url)
    }

    private fun observeWeatherData() = coroutineScope.launch {
        combine(
            weatherRepository.weatherData,
            userPreferencesRepository.weatherUnitEnum
        ) { weatherData, weatherUnit ->
            Pair(weatherData, weatherUnit)
        }.collectLatest { (weatherData, weatherUnit) ->
            if (weatherData != null) {
                val currentWeatherState = weatherData.toCurrentWeatherState(weatherUnit)
                val forecastWeatherState = weatherData.forecast?.toForecastWeatherState(weatherUnit) ?: emptyList()
                _state.update {
                    it.copy(
                        weatherState = it.weatherState.copy(
                            currentWeather = currentWeatherState,
                            forecastWeather = forecastWeatherState,
                            weatherUnits = weatherUnit.name,
                            scrollToForecastCurrentTimeIndex = calculateCurrentForecastIndex(weatherData.forecast ?: emptyList())
                        )
                    )
                }
            }
        }
    }

    private fun calculateCurrentForecastIndex(
        forecast: List<WeatherData>,
    ): Int {
        return forecast.indexOfFirst { item ->
            item.time?.hour == DateUtils.getCurrentDateTime().hour
        }.coerceAtLeast(0)
    }

    private fun observeTodoData() = coroutineScope.launch {
        launch {
            todoistRepository.tasks.collectLatest { tasks ->
                _state.update {
                    it.copy(
                        todoState = it.todoState.copy(
                            todoData = tasks.toTodoDataState(),
                            isLoading = false
                        )
                    )
                }
            }
        }
        launch {
            todoistRepository.isConnected.collectLatest { isConnected ->
                _state.update {
                    it.copy(
                        todoState = it.todoState.copy(
                            isTodoAuthenticated = isConnected
                        )
                    )
                }
                if (isConnected) {
                    refreshTodoTasks()
                }
            }
        }
    }

    private fun fetchCalendarEvents() = coroutineScope.launch {
        if (_isCalendarPermissionGranted.value) {
            val events = calendarEventsRepository.getCalendarEvents()
            _state.update {
                it.copy(
                    calendarData = it.calendarData.copy(
                        calendarData = events,
                        isCalendarPermissionGranted = true
                    )
                )
            }
        }
    }

    private fun requestTodoistAuthentication() = coroutineScope.launch {
        _state.update { it.copy(todoState = it.todoState.copy(error = null)) }
        todoistRepository.requestAuthorization()
    }

    private fun handleAuthorizationCode(code: String) = coroutineScope.launch {
        _state.update { it.copy(todoState = it.todoState.copy(isAuthenticating = true, error = null)) }
        todoistRepository.exchangeToken(code)
            .onSuccess {
                _state.update { it.copy(todoState = it.todoState.copy(isAuthenticating = false, error = null)) }
                refreshTodoTasks()
            }
            .onFailure { error ->
                val message = error.message ?: "Authentication failed"
                _state.update { it.copy(todoState = it.todoState.copy(isAuthenticating = false, error = message)) }
                sendEffect(HomeEffect.ShowToast(message))
            }
    }

    private fun refreshTodoTasks() = coroutineScope.launch {
        _state.update { it.copy(todoState = it.todoState.copy(isLoading = true, error = null)) }
        todoistRepository.refreshTasks()
            .onSuccess {
                _state.update { it.copy(todoState = it.todoState.copy(isLoading = false, error = null)) }
            }
            .onFailure { error ->
                val message = error.message ?: "Failed to fetch tasks"
                if (message == ErrorCodes.UNAUTHORIZED.name) {
                    disconnectTodoist()
                    sendEffect(HomeEffect.ShowToast("Session expired. Please connect again."))
                } else {
                    _state.update { it.copy(todoState = it.todoState.copy(isLoading = false, error = message)) }
                    sendEffect(HomeEffect.ShowToast(message))
                }
            }
    }

    private fun disconnectTodoist() = coroutineScope.launch {
        todoistRepository.disconnect()
        _state.update {
            it.copy(
                todoState = it.todoState.copy(
                    isTodoAuthenticated = false,
                    todoToken = null,
                    todoData = null,
                    isLoading = false
                )
            )
        }
    }

    private fun sendEffect(effect: HomeEffect) = coroutineScope.launch {
        _effects.emit(effect)
    }

    private fun generateSummary(forceRefresh: Boolean = false) = coroutineScope.launch {
        if (localLLMRepo.isModelDownloaded()) {
            combine(
                state.map { it.weatherState },
                state.map { it.todoState },
                state.map { it.calendarData }
            ) { weatherState, todoState, calendarState ->
                val weatherReady = !weatherState.isLoading
                val todoReady = !todoState.isLoading
                val calendarReady = !calendarState.isLoading
                weatherReady && calendarReady && todoReady
            }
                .filter { it }
                .first()

            localLLMRepo.generateDaySummary(
                input = _state.value.generateSummaryPrompt(),
                forceRefresh = forceRefresh
            )
                .collect { event ->
                    when (event) {
                        is LlmGenerationEvent.Failed -> {
                            updateAiResponseState {
                                it.copy(
                                    isGenerating = false,
                                    isModelDownloaded = true,
                                    error = event.message
                                )
                            }
                            sendEffect(HomeEffect.ShowToast("Failed to generate summary: ${event.message}"))
                        }

                        is LlmGenerationEvent.Completed -> {
                            updateAiResponseState {
                                it.copy(
                                    isGenerating = false,
                                    isModelDownloaded = true,
                                    error = null,
                                    aiResponse = event.response.aiResponse,
                                    thinkingResponse = event.response.thinkingResponse,
                                    modelName = event.response.modelName,
                                    isThinking = event.response.isThinking
                                )
                            }
                        }

                        is LlmGenerationEvent.Streaming -> {
                            updateAiResponseState {
                                it.copy(
                                    isGenerating = true,
                                    isModelDownloaded = true,
                                    error = null,
                                    aiResponse = event.response.aiResponse,
                                    thinkingResponse = event.response.thinkingResponse,
                                    modelName = event.response.modelName,
                                    isThinking = event.response.isThinking
                                )
                            }
                        }
                    }
                }
        } else {
            _state.update {
                it.copy(
                    aiResponseState = it.aiResponseState.copy(
                        isModelDownloaded = false,
                        error = "AI model not downloaded"
                    )
                )
            }
        }
    }

    private fun observeCachedSummary() = coroutineScope.launch {
        runCatching { localLLMRepo.isModelDownloaded() }
            .onSuccess { isDownloaded ->
                _state.update {
                    it.copy(
                        aiResponseState = it.aiResponseState.copy(
                            isModelDownloaded = isDownloaded
                        )
                    )
                }
            }
        localLLMRepo.cachedSummary.collectLatest { cached ->
            if (cached == null) return@collectLatest
            updateAiResponseState { current ->
                if (current.isGenerating) return@updateAiResponseState current
                current.copy(
                    isModelDownloaded = true,
                    aiResponse = cached.aiResponse,
                    thinkingResponse = cached.thinkingResponse,
                    modelName = cached.modelName,
                    isThinking = cached.isThinking
                )
            }
        }
    }

    private inline fun updateAiResponseState(transform: (AIResponseState) -> AIResponseState) {
        _state.update { it.copy(aiResponseState = transform(it.aiResponseState)) }
    }
}
