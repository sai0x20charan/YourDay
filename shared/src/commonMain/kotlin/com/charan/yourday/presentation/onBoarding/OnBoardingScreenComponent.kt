package com.charan.yourday.presentation.onBoarding

import com.arkivanov.decompose.ComponentContext
import com.charan.yourday.data.repository.TodoistRepo
import com.charan.yourday.permission.PermissionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class OnBoardingScreenComponent (
    val componentContext: ComponentContext,
    val onFinish : () -> Unit,
    val authorizationId : String?,
) : ComponentContext by componentContext, KoinComponent{

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val todoistRepo: TodoistRepo = get()
    private var _onBoardingState = MutableStateFlow(OnBoardingState())
    var onBoardingState = _onBoardingState.asStateFlow()
    private var _onBoardingEvent = MutableSharedFlow<OnBoardingViewEffect>()
    var onBoardingEvent = _onBoardingEvent.asSharedFlow()
    private val permissionManager: PermissionManager = get()

    fun onEvent(intent: OnBoardingEvent){
        when(intent){
            is OnBoardingEvent.onCalenderPermissionGrant -> {
                handleCalenderPermission(intent.shouldShowRationale)

            }
            is OnBoardingEvent.onLocationPermissionGrant -> {
                handleLocationPermission(intent.shouldShowRationale)


            }
            OnBoardingEvent.onTodoistConnect -> {
                connectToTodoist()
            }
        }
    }

    private fun connectToTodoist() = coroutineScope.launch{
        todoistRepo.requestAuthorization()

    }

    private fun handleLocationPermission(showShowRationale :Boolean) = coroutineScope.launch {
        if (!showShowRationale) {
            _onBoardingEvent.emit(OnBoardingViewEffect.OnLocationPermissionRequest)

        } else {
            permissionManager.openAppSettings()
        }

    }

    private fun handleCalenderPermission(showShowRationale :Boolean) = coroutineScope.launch {
        if (!showShowRationale) {
            _onBoardingEvent.emit(OnBoardingViewEffect.OnCalenderPermissionRequest)

        } else {
            permissionManager.openAppSettings()
        }

    }



}