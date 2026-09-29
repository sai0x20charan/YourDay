package com.charan.yourday.presentation.home

import com.arkivanov.decompose.ComponentContext
import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.data.network.responseDTO.ForecastClass
import com.charan.yourday.data.network.responseDTO.TodoistTokenDTO
import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.data.repository.DataStoreRepository
import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.TodoistRepo
import com.charan.yourday.data.repository.WeatherRepo
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.presentation.toCurrentWeatherState
import com.charan.yourday.presentation.toForecastWeatherState
import com.charan.yourday.presentation.toTodoDataState
import com.charan.yourday.utils.DateUtils
import com.charan.yourday.utils.DateUtils.getGreeting
import com.charan.yourday.utils.DateUtils.toDDMMYYYY
import com.charan.yourday.utils.ErrorCodes
import com.charan.yourday.utils.OpenURL
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.asCommonFlow
import com.splendo.kaluga.permissions.base.Permission
import com.splendo.kaluga.permissions.base.PermissionState
import com.splendo.kaluga.permissions.base.Permissions
import com.splendo.kaluga.permissions.base.PermissionsBuilder
import com.splendo.kaluga.permissions.calendar.CalendarPermission
import com.splendo.kaluga.permissions.location.LocationPermission
import com.splendo.kaluga.permissions.location.registerLocationPermission
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.any
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class HomeScreenComponent(
    val authorizationId: String?,
    private val errorCode: String?,
    private val onSettingsOpen: () -> Unit = {},
    private val onBoardFinish: () -> Unit = {},
    componentContext: ComponentContext
) : KoinComponent, ComponentContext by componentContext {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeEffect>()
    val effects = _effects.asSharedFlow()


    private val weatherRepo: WeatherRepo = get()
    private val locationServiceRepo: LocationServiceRepo = get()
    private val permissionManager: PermissionManager = get()
    private val calendarEventsRepo: CalenderEventsRepo = get()
    private val todoistRepo: TodoistRepo = get()
    private val dataStoreRepo: DataStoreRepository = get()

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
        observerWeatherData()
        observeTodoData()
        coroutineScope.launch {
            authorizationId?.let {
                getTodoistAccessToken(it)
            }
            errorCode?.let { sendEffect(HomeEffect.ShowToast("Unable to authenticate")) }
        }
        refreshData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.RequestLocationPermission -> handleLocationPermission()
            is HomeEvent.RequestCalendarPermission -> handleCalendarPermission()
            HomeEvent.ConnectTodoist -> requestTodoistAuthentication()
            HomeEvent.FetchWeather -> fetchLocationAndWeather()
            HomeEvent.FetchCalendarEvents -> fetchCalendarEvents()
            HomeEvent.DisconnectTodoist -> clearTodoistToken()
            HomeEvent.OpenSettingsPage -> {
                updateDropdownMenuState(false)
                onSettingsOpen()
            }
            is HomeEvent.OnOpenLink -> openURL(event.url)
            HomeEvent.FetchTodo -> checkTokenAndFetchTasks()
            HomeEvent.RefreshData -> refreshData()
            HomeEvent.OnBoardingFinish -> {
                onBoardFinish()
            }
            is HomeEvent.ShowDropdownMenu -> {
                updateDropdownMenuState(event.show)
            }
        }
    }

    private fun updateDropdownMenuState(show: Boolean) {
        _state.update {
            it.copy(
                showDropDown = show
            )
        }
    }

    private fun refreshData() {
        refreshDateAndGreetings()
        fetchLocationAndWeather()
        fetchCalendarEvents()
        checkTokenAndFetchTasks()
        updateDropdownMenuState(false)
    }


    private fun refreshDateAndGreetings(){
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
        permissions[calendarPermission].filter { it !is PermissionState.Uninitialized }.collectLatest { permissionState ->
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
                sendEffect(HomeEffect.ShowToast("Unable to fetch location"))
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

    private fun fetchWeatherData(lat: Double, long: Double) = coroutineScope.launch {
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
                    sendEffect(HomeEffect.ShowToast(processState.message))
                }

                ProcessState.Loading -> {
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
                        weatherUnits = weatherUnit.name,
                        scrollToForecastCurrentTimeIndex = calculateCurrentForecastIndex(weatherData.forecast ?: emptyList())
                    )
                )
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
                    sendEffect(HomeEffect.ShowToast(processState.message))
                }

                ProcessState.Loading -> {
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
            }
        }
    }

    private fun fetchTodoistTasks(token: String) = coroutineScope.launch {
        todoistRepo.getTodayTasks(token).collectLatest { processState ->
            when (processState) {
                is ProcessState.Error -> {
                    handleTodoistTasksError(processState.message)

                }

                ProcessState.Loading -> {
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
            }
        }
    }

    private fun handleTodoistTasksError(message: String) {
        if (message == ErrorCodes.UNAUTHORIZED.name) {
            clearTodoistToken()
            sendEffect(HomeEffect.ShowToast("Session expired. Please connect again."))
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
            sendEffect(HomeEffect.ShowToast(message))
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
                            todoData = null,
                            isLoading = false
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

    private fun sendEffect(effect: HomeEffect) = coroutineScope.launch {
        _effects.emit(effect)
    }

}
