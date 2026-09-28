package com.charan.yourday.presentation.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.subscribe
import com.charan.yourday.data.repository.A2uiRepository
import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.data.repository.DataStoreRepository
import com.charan.yourday.data.repository.LocalLLMRepository
import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.TodoistRepo
import com.charan.yourday.data.repository.WeatherRepo
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.presentation.utils.SummaryPromptBuilder.generateSummaryPrompt
import com.charan.yourday.presentation.utils.toCurrentWeatherState
import com.charan.yourday.presentation.utils.toForecastWeatherState
import com.charan.yourday.presentation.utils.toTodoDataState
import com.charan.yourday.utils.ErrorCodes
import com.charan.yourday.utils.OpenURL
import com.charan.yourday.utils.ProcessState
import com.splendo.kaluga.permissions.base.PermissionState
import com.splendo.kaluga.permissions.base.Permissions
import com.splendo.kaluga.permissions.base.PermissionsBuilder
import com.splendo.kaluga.permissions.calendar.CalendarPermission
import com.splendo.kaluga.permissions.location.LocationPermission
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


class HomeScreenComponent(
    val authorizationId: String?,
    private val errorCode: String?,
    private val onSettingsOpen: () -> Unit = {},
    private val onBoardFinish: () -> Unit = {},
    private val isResumed : Boolean,
    componentContext: ComponentContext
) : KoinComponent, ComponentContext by componentContext {

    val json = """
[
  {
    "version": "v0.9",
    "createSurface": {
      "surfaceId": "sample-surface",
      "catalogId": "https://a2ui.org/specification/v0_9/catalogs/basic/catalog.json",
      "sendDataModel": true
    }
  },
  {
    "version": "v0.9",
    "updateComponents": {
      "surfaceId": "sample-surface",
      "components": [
        {
          "id": "root",
          "component": "Column",
          "children": [
            "title",
            "location_input",
            "pickup_input",
            "dropoff_input",
            "book_button"
          ],
          "justify": "start",
          "align": "stretch"
        },
        {
          "id": "title",
          "component": "Text",
          "text": "Book a Car",
          "variant": "h1"
        },
        {
          "id": "location_input",
          "component": "TextField",
          "label": "Pick-up Location",
          "value": {
            "path": "/booking/location"
          },
          "variant": "shortText"
        },
        {
          "id": "pickup_input",
          "component": "DateTimeInput",
          "label": "Pick-up Date",
          "value": {
            "path": "/booking/pickupDate"
          },
          "enableDate": true,
          "enableTime": false
        },
        {
          "id": "dropoff_input",
          "component": "DateTimeInput",
          "label": "Drop-off Date",
          "value": {
            "path": "/booking/dropoffDate"
          },
          "enableDate": true,
          "enableTime": false
        },
        {
          "id": "book_button",
          "component": "Button",
          "child": "book_button_text",
          "variant": "primary",
          "action": {
            "event": {
              "name": "searchCars",
              "context": {
                "location": {
                  "path": "/booking/location"
                },
                "pickupDate": {
                  "path": "/booking/pickupDate"
                },
                "dropoffDate": {
                  "path": "/booking/dropoffDate"
                }
              }
            }
          }
        },
        {
          "id": "book_button_text",
          "component": "Text",
          "text": "Search Cars",
          "variant": "body"
        }
      ]
    }
  },
  {
    "version": "v0.9",
    "updateDataModel": {
      "surfaceId": "sample-surface",
      "path": "/booking",
      "value": {
        "location": "",
        "pickupDate": "",
        "dropoffDate": ""
      }
    }
  }
]
""".trimIndent()

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeViewEffect>()
    val effects = _effects.asSharedFlow()




    private val weatherRepo: WeatherRepo = get()
    private val locationServiceRepo: LocationServiceRepo = get()
    private val permissionManager: PermissionManager = get()
    private val calendarEventsRepo: CalenderEventsRepo = get()
    private val todoistRepo: TodoistRepo = get()
    private val dataStoreRepo: DataStoreRepository = get()
    private val localLLMRepo: LocalLLMRepository = get()

    private val permissionsBuilder: PermissionsBuilder = get()

    private val a2uiRepository : A2uiRepository = get()

    private val permissions = Permissions(permissionsBuilder)

    private val locationPermission = LocationPermission(background = false, precise = true)

    private val calendarPermission = CalendarPermission()

    // Cached permission states
    private val _isLocationPermissionGranted = MutableStateFlow(false)
    private val _isCalendarPermissionGranted = MutableStateFlow(false)

    val surfaces = a2uiRepository.surfaces




    init {
        println("HomeScreenComponent initialized")

        observeLocationPermission()
        observeCalendarPermission()
        observerWeatherData()
        observeTodoData()
        coroutineScope.launch {
            authorizationId?.let {
                getTodoistAccessToken(it)
            }
            errorCode?.let { sendEffect(HomeViewEffect.ShowToast("Unable to authenticate")) }
        }
        refreshData()
        lifecycle.subscribe(
            onResume = {
                if (!_state.value.aiResponseState.isModelDownloaded) {
                    generateSummary()
                }
            }
        )


    }


    fun onEvent(intent: HomeEvent) {
        when (intent) {
            is HomeEvent.RequestLocationPermission -> handleLocationPermission()
            is HomeEvent.RequestCalendarPermission -> handleCalendarPermission()
            HomeEvent.ConnectTodoist -> requestTodoistAuthentication()
            HomeEvent.FetchWeather -> fetchLocationAndWeather()
            HomeEvent.FetchCalendarEvents -> fetchCalendarEvents()
            HomeEvent.DisconnectTodoist -> clearTodoistToken()
            HomeEvent.OpenSettingsPage -> onSettingsOpen()
            is HomeEvent.OnOpenLink -> openURL(intent.url)
            HomeEvent.FetchTodo -> checkTokenAndFetchTasks()
            HomeEvent.RefreshData -> refreshData()
            HomeEvent.OnBoardingFinish -> {
                onBoardFinish()
            }
            HomeEvent.OnGenerateAIResponse -> generateSummary()

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

    private fun refreshData() {
        fetchLocationAndWeather()
        fetchCalendarEvents()
        checkTokenAndFetchTasks()
    }

    private fun observeLocationPermission() = coroutineScope.launch {
        permissions[locationPermission].collectLatest { permissionState ->
            val isGranted = permissionState is PermissionState.Allowed
            _isLocationPermissionGranted.value = isGranted

            _state.update {
                it.copy(
                    weatherState = it.weatherState.copy(
                        isLocationPermissionGranted = isGranted
                    )
                )
            }


            if (isGranted) {
                fetchLocationAndWeather()
            }
        }
    }

    private fun observeCalendarPermission() = coroutineScope.launch {
        permissions[calendarPermission].collectLatest { permissionState ->
            val isGranted = permissionState is PermissionState.Allowed
            _isCalendarPermissionGranted.value = isGranted

            _state.update {
                it.copy(
                    calenderData = it.calenderData.copy(
                        isCalenderPermissionGranted = isGranted
                    )
                )
            }

            if (isGranted) {
                fetchCalendarEvents()
            }
        }
    }

    private fun handleLocationPermission() = coroutineScope.launch {
        when (val state = permissions[locationPermission].peekState()) {
            is PermissionState.Allowed -> {

                fetchLocationAndWeather()
            }

            is PermissionState.Denied.Requestable,
            is PermissionState.Uninitialized -> {

                permissions.request(locationPermission)

            }

            is PermissionState.Denied.Locked -> {

                permissionManager.openAppSettings()

            }

            else -> {}
        }
    }

    private fun handleCalendarPermission() = coroutineScope.launch {
        when (val state = permissions[calendarPermission].peekState()) {
            is PermissionState.Allowed -> {

                fetchCalendarEvents()
            }

            is PermissionState.Denied.Requestable,
            is PermissionState.Uninitialized -> {

                permissions.request(calendarPermission)

            }

            is PermissionState.Denied.Locked -> {
                permissionManager.openAppSettings()
            }

            else -> {}
        }
    }

    private fun fetchLocationAndWeather() = coroutineScope.launch {
        if (_isLocationPermissionGranted.value) {
            _state.update {
                it.copy(
                    weatherState = it.weatherState.copy(
                        isLoading = true,
                        error = null
                    )
                )
            }
            val location = locationServiceRepo.getCurrentLocation()
            if (location != null) {
                fetchWeatherData(location.latitude!!, location.longitude!!)
            } else {
                sendEffect(HomeViewEffect.ShowToast("Unable to fetch location"))
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
    }

    private fun openURL(url: String) {
        OpenURL.openURL(url)
    }

    private suspend fun fetchWeatherData(lat: Double, long: Double) {
        weatherRepo.getCurrentForecast(lat, long).collectLatest { processState ->

            when (processState) {
                is ProcessState.Error -> {
                    _state.update {
                        it.copy(
                            weatherState = it.weatherState.copy(
                                isLoading = false,
                                error = processState.message
                            )
                        )
                    }
                    sendEffect(HomeViewEffect.ShowToast(processState.message))
                }

                is ProcessState.Loading -> {
                    _state.update {
                        it.copy(
                            weatherState = it.weatherState.copy(
                                isLoading = true,
                                error = null
                            )
                        )
                    }
                }

                ProcessState.NotDetermined -> {}
                is ProcessState.Success -> {
                    _state.update {
                        it.copy(
                            weatherState = it.weatherState.copy(
                                isLoading = false,
                                error = null,
                            )
                        )
                    }
                }

                else -> {}
            }
        }
    }

    private fun observerWeatherData() = coroutineScope.launch {
        combine(
            dataStoreRepo.weatherData,
            dataStoreRepo.weatherUnit
        ) { weatherData, weatherUnit ->
            Pair(weatherData, weatherUnit)
        }.collectLatest { (weatherData, weatherUnit) ->
            val currentWeatherState = weatherData.toCurrentWeatherState(weatherUnit)
            val forecastWeatherState = weatherData.forecast?.toForecastWeatherState(weatherUnit)
            _state.update {
                it.copy(
                    weatherState = it.weatherState.copy(
                        currentWeather = currentWeatherState,
                        forecastWeather = forecastWeatherState ?: emptyList(),
                        weatherUnits = weatherUnit.name
                    )
                )
            }
        }
    }

    private fun observeTodoData() = coroutineScope.launch {
        dataStoreRepo.todoData.collectLatest { todoData ->
            val todoDataState = todoData.toTodoDataState()
            _state.update {
                it.copy(
                    todoState = it.todoState.copy(
                        todoData = todoDataState
                    )
                )
            }
        }
    }

    private fun fetchCalendarEvents() = coroutineScope.launch {
        if (_isCalendarPermissionGranted.value) {
            _state.update {
                it.copy(
                    calenderData = it.calenderData.copy(
                        calenderData = calendarEventsRepo.getCalenderEvents()
                    )
                )
            }
        }
    }


    private fun requestTodoistAuthentication() = coroutineScope.launch {
        _state.update {
            it.copy(
                todoState = it.todoState.copy(
                    isAuthenticating = true,
                    error = null
                )
            )
        }
        todoistRepo.requestAuthorization()
    }

    private fun getTodoistAccessToken(authorizationId: String) = coroutineScope.launch {
        todoistRepo.getAccessToken(authorizationId).collectLatest { processState ->
            when (processState) {
                is ProcessState.Error -> {
                    _state.update {
                        it.copy(
                            todoState = it.todoState.copy(
                                isAuthenticating = true,
                                error = processState.message
                            )
                        )
                    }
                    sendEffect(HomeViewEffect.ShowToast(processState.message))
                }

                is ProcessState.Loading -> {
                    _state.update {
                        it.copy(
                            todoState = it.todoState.copy(
                                isAuthenticating = true,
                                error = null
                            )
                        )
                    }
                }

                ProcessState.NotDetermined -> {}
                is ProcessState.Success -> {
                    _state.update {
                        it.copy(
                            todoState = it.todoState.copy(
                                isAuthenticating = false,
                                isTodoAuthenticated = true,
                                error = null,
                            )
                        )
                    }
                    fetchTodoistTasks(processState.data.access_token ?: "")
                }

                else -> {}
            }
        }
    }

    private fun fetchTodoistTasks(token: String) = coroutineScope.launch {
        todoistRepo.getTodayTasks(token).collectLatest { processState ->
            when (processState) {
                is ProcessState.Error -> {
                    handleTodoistTasksError(processState.message)

                }

                is ProcessState.Loading -> {
                    _state.update {
                        it.copy(
                            todoState = it.todoState.copy(
                                isLoading = true,
                                error = null
                            )
                        )
                    }
                }

                ProcessState.NotDetermined -> { /* No action needed */
                }

                is ProcessState.Success -> {
                    _state.update {
                        it.copy(
                            todoState = it.todoState.copy(
                                isLoading = false,
                                error = null,
                            )
                        )
                    }
                }

                else -> {}
            }
        }
    }

    private fun handleTodoistTasksError(message: String) {
        if (message == ErrorCodes.UNAUTHORIZED.name) {
            clearTodoistToken()
            sendEffect(HomeViewEffect.ShowToast("Session expired. Please connect again."))
            _state.update {
                it.copy(
                    todoState = it.todoState.copy(
                        isLoading = false,
                        error = "Session expired. Please connect again.",
                        isTodoAuthenticated = false
                    )
                )
            }
        } else {
            _state.update {
                it.copy(
                    todoState = it.todoState.copy(
                        isLoading = false,
                        error = message
                    )
                )
            }
            sendEffect(HomeViewEffect.ShowToast(message))
        }

    }

    private fun checkTokenAndFetchTasks() = coroutineScope.launch {
        dataStoreRepo.todoistAccessToken.collectLatest { token ->
            if (token == null) {
                _state.update {
                    it.copy(
                        todoState = it.todoState.copy(
                            isTodoAuthenticated = false,
                            todoToken = null,
                            todoData = null
                        )
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        todoState = it.todoState.copy(
                            isTodoAuthenticated = true,
                            todoToken = token
                        )
                    )
                }
                fetchTodoistTasks(token)

            }
        }
    }

    private fun clearTodoistToken() = coroutineScope.launch {
        dataStoreRepo.setTodoistAccessToken("")
        _state.update {
            it.copy(
                todoState = it.todoState.copy(
                    isTodoAuthenticated = false,
                    todoToken = null,
                    todoData = null
                )
            )
        }
    }

    private fun sendEffect(effect: HomeViewEffect) = coroutineScope.launch {
        _effects.emit(effect)
    }

    private fun checkIfModelIsDownloaded() = coroutineScope.launch {
//        _state.update {
//            it.copy(
//                aiResponseState = it.aiResponseState.copy(
//                    isModelDownloaded = localLLMRepo.isModelDownloaded()
//                )
//            )
//        }
    }

    private fun generateSummary() = coroutineScope.launch {



        if (localLLMRepo.isModelDownloaded()) {
            println(localLLMRepo.isModelDownloaded())
            combine(
                state.map { it.weatherState },
                state.map { it.todoState },
                state.map { it.calenderData }
            ) { weatherState, todoState, calendarState ->
                val weatherReady = !weatherState.isLoading
                val todoReady = !todoState.isLoading
                val calendarReady = !calendarState.isLoading
                weatherReady && calendarReady && todoReady
            }
                .filter { it }
                .first()


            localLLMRepo.generateDaySummary(input = _state.value.generateSummaryPrompt())
                .collect { processState ->
                    when (processState) {
                        is ProcessState.Error -> {
                            sendEffect(HomeViewEffect.ShowToast("Failed to generate summary: ${processState.message}"))
                        }

                        is ProcessState.Loading -> {
                            _state.update {
                                it.copy(
                                    aiResponseState = it.aiResponseState.copy(
                                        isGenerating = true,
                                        error = null,
                                        isModelDownloaded = true
                                    )
                                )
                            }
                        }

                        ProcessState.NotDetermined -> {}
                        is ProcessState.Success -> {
                            a2uiRepository.process(processState.data.aiResponse)
                            println(processState.data.aiResponse)
                            _state.update {
                                it.copy(
                                    aiResponseState = it.aiResponseState.copy(
                                        isGenerating = false,
                                        error = null,
                                        aiResponse = processState.data.aiResponse,
                                        thinkingResponse = processState.data.thinkingResponse,
                                        modelName = processState.data.modelName,
                                        isThinking = processState.data.isThinking
                                    )
                                )
                            }

                        }

                        is ProcessState.Streaming -> {
                            _state.update {
                                it.copy(
                                    aiResponseState = it.aiResponseState.copy(
                                        isGenerating = true,
                                        error = null,
                                        aiResponse = processState.partialData.aiResponse,
                                        thinkingResponse = processState.partialData.thinkingResponse,
                                        modelName = processState.partialData.modelName,
                                        isThinking = processState.partialData.isThinking
                                    )
                                )
                            }
                        }

                        else -> {}

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
}



