package com.charan.yourday.presentation.onboarding

import com.arkivanov.decompose.ComponentContext
import com.charan.yourday.data.repository.TodoistRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.PermissionType
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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class OnBoardingScreenComponent(
    componentContext: ComponentContext,
    val onFinish: () -> Unit,
    val authorizationId: String? = null,
) : ComponentContext by componentContext, KoinComponent {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val todoistRepository: TodoistRepository = get()
    private val userPreferencesRepository: UserPreferencesRepository = get()
    private val permissionManager: PermissionManager = get()
    private val permissionsBuilder: PermissionsBuilder = get()
    private val permissions = Permissions(permissionsBuilder)
    private val locationPermission = LocationPermission(background = false, precise = true)
    private val calendarPermission = CalendarPermission()

    private val _onBoardingState = MutableStateFlow(OnBoardingState())
    val onBoardingState = _onBoardingState.asStateFlow()

    private val _onBoardingEffect = MutableSharedFlow<OnBoardingViewEffect>()
    val onBoardingEffect = _onBoardingEffect.asSharedFlow()

    init {
        observeLocationPermission()
        observeCalendarPermission()
        observeTodoistAuth()

        authorizationId?.let {
            coroutineScope.launch {
                todoistRepository.exchangeToken(it).onSuccess {
                    _onBoardingState.update { state -> state.copy(isTodoistConnected = true) }
                }
            }
        }
    }

    private fun observeLocationPermission() = coroutineScope.launch {
        permissions[locationPermission].filter { it !is PermissionState.Uninitialized }.collectLatest { permissionState ->
            val isGranted = permissionState is PermissionState.Allowed
            _onBoardingState.update { it.copy(isLocationPermissionGranted = isGranted) }
        }
    }

    private fun observeCalendarPermission() = coroutineScope.launch {
        permissions[calendarPermission].filter { it !is PermissionState.Uninitialized }.collectLatest { permissionState ->
            val isGranted = permissionState is PermissionState.Allowed
            _onBoardingState.update { it.copy(isCalendarPermissionGranted = isGranted) }
        }
    }

    private fun observeTodoistAuth() = coroutineScope.launch {
        userPreferencesRepository.todoistAccessToken.collectLatest { token ->
            _onBoardingState.update { it.copy(isTodoistConnected = !token.isNullOrEmpty()) }
        }
    }

    fun onEvent(event: OnBoardingEvent) {
        when (event) {
            is OnBoardingEvent.RequestLocationPermission,
            is OnBoardingEvent.onLocationPermissionGrant -> handleLocationPermission()

            is OnBoardingEvent.RequestCalendarPermission,
            is OnBoardingEvent.onCalenderPermissionGrant -> handleCalendarPermission()

            is OnBoardingEvent.ConnectTodoist,
            is OnBoardingEvent.onTodoistConnect -> connectTodoist()

            is OnBoardingEvent.Finish -> onFinish()
            is OnBoardingEvent.CheckPermissions -> checkPermissions()
        }
    }

    private fun handleLocationPermission() = coroutineScope.launch {
        when (permissions[locationPermission].peekState()) {
            is PermissionState.Allowed -> {
                _onBoardingState.update { it.copy(isLocationPermissionGranted = true) }
            }
            is PermissionState.Denied.Requestable,
            is PermissionState.Uninitialized -> permissions.request(locationPermission)
            is PermissionState.Denied.Locked -> permissionManager.openAppSettings()
            else -> {}
        }
    }

    private fun handleCalendarPermission() = coroutineScope.launch {
        when (permissions[calendarPermission].peekState()) {
            is PermissionState.Allowed -> {
                _onBoardingState.update { it.copy(isCalendarPermissionGranted = true) }
            }
            is PermissionState.Denied.Requestable,
            is PermissionState.Uninitialized -> permissions.request(calendarPermission)
            is PermissionState.Denied.Locked -> permissionManager.openAppSettings()
            else -> {}
        }
    }

    private fun connectTodoist() = coroutineScope.launch {
        todoistRepository.requestAuthorization()
    }

    private fun checkPermissions() {
        _onBoardingState.update {
            it.copy(
                isLocationPermissionGranted = permissionManager.isPermissionGranted(PermissionType.LOCATION),
                isCalendarPermissionGranted = permissionManager.isPermissionGranted(PermissionType.CALENDAR)
            )
        }
    }
}
