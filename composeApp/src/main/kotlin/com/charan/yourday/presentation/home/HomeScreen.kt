package com.charan.yourday.presentation.home

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.charan.yourday.presentation.home.components.CalendarCard
import com.charan.yourday.presentation.home.components.TodoCard
import com.charan.yourday.presentation.home.components.WeatherCard
import com.charan.yourday.utils.DateUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class,
    ExperimentalMaterialApi::class, ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun HomeScreen(
    component : HomeScreenComponent,
) {
    val context = LocalContext.current
    val homeState by component.state.collectAsState()


    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val calenderPermissionState = rememberPermissionState(Manifest.permission.READ_CALENDAR)
    val scroll = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pullToRefreshState = rememberPullRefreshState(
        refreshing = homeState.isRefreshing,
        onRefresh = {component.onEvent(HomeEvent.RefreshData)},
    )
    val listState = rememberLazyListState()
    val isThresholdReached by remember {
        derivedStateOf {
            pullToRefreshState.progress.dp >=1.dp
        }
    }
    val isPulledDown by remember {
        derivedStateOf {
            pullToRefreshState.progress.dp > 0.dp
        }
    }

    var showDropDown by remember { mutableStateOf(false) }

    LaunchedEffect(component.effects) {
        component.effects.collectLatest {
            when (it) {
                is HomeViewEffect.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }

                HomeViewEffect.RequestCalenderPermission -> {
                    calenderPermissionState.launchPermissionRequest()
                }

                HomeViewEffect.RequestLocationPermission -> {
                    locationPermissionState.launchPermissionRequest()
                }
            }
        }
    }
    LaunchedEffect(locationPermissionState.status) {
        when (locationPermissionState.status) {
            is PermissionStatus.Denied -> {}
            PermissionStatus.Granted -> {
                Log.d("TAG", "HomeScreen: granted")
                component.onEvent(HomeEvent.FetchWeather)
            }
        }
    }
    LaunchedEffect(calenderPermissionState.status) {
        when (calenderPermissionState.status) {
            is PermissionStatus.Denied -> {}
            PermissionStatus.Granted -> {
                component.onEvent(HomeEvent.FetchCalendarEvents)

            }
        }
    }




    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                   Text(DateUtils.getGreeting())

                },
                subtitle = {
                    Text(DateUtils.getDateInDDMMYYYY())
                },
                scrollBehavior = scroll,
                actions = {
                    IconButton(
                        onClick = {
                            showDropDown = true
                        },
                        shapes = IconButtonDefaults.shapes(),

                    ) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    DropdownMenu(
                        expanded = showDropDown,
                        onDismissRequest = {
                            showDropDown = false
                        }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                showDropDown = false
                                component.onEvent(HomeEvent.OpenSettingsPage)
                            },
                            text = {
                                Text("Settings")
                            }
                        )
                    }
                }
            )

        },
        modifier = Modifier
    ) { padding ->



            LazyColumn(
                state = listState,
                contentPadding = padding,
                modifier = Modifier
                    .nestedScroll(scroll.nestedScrollConnection)
                    .fillMaxSize()
                    .pullRefresh(state = pullToRefreshState)
                    .offset(y = pullToRefreshState.progress.dp * 8)
                    .padding(15.dp)

            ) {
                item {
                    AnimatedVisibility(
                        modifier = Modifier,
                        visible = isPulledDown,
                        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)


                    ) {
                    Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center) {
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            val refreshText = if(isThresholdReached) "Release to refresh" else "Pull to refresh"
                            CircularWavyProgressIndicator(
                                progress = {
                                    pullToRefreshState.progress
                                },
                                modifier = Modifier.size(25.dp),
                            )
                            Spacer(Modifier.padding(end = 8.dp))
                            Text(refreshText,modifier = Modifier.animateContentSize())
                        }
                    }
                    }

                    WeatherCard(
                        isLoading = homeState.weatherState.isLoading,
                        error = homeState.weatherState.error,
                        hasContent =  homeState.weatherState.currentWeather != null,
                        location = homeState.weatherState.currentWeather?.location,
                        currentTemperature = homeState.weatherState.currentWeather?.temp.toString(),
                        currentWeatherIcon = homeState.weatherState.currentWeather?.icon,
                        forecastData = homeState.weatherState.forecastWeather,
                        isPermissionGranted = homeState.weatherState.isLocationPermissionGranted,
                        weatherConditionText = homeState.weatherState.currentWeather?.condition.orEmpty(),
                        weatherUnits = homeState.weatherState.weatherUnits,
                        onLocationPermissionAccess = {
                            component.onEvent(
                                HomeEvent.RequestLocationPermission(
                                    locationPermissionState.status.shouldShowRationale
                                )
                            )
                        }
                    )
                    Spacer(Modifier.padding(vertical = 10.dp))
                    CalendarCard(
                        calenderState = homeState.calenderData,
                        grantPermission = {
                            component.onEvent(
                                HomeEvent.RequestCalendarPermission(
                                    calenderPermissionState.status.shouldShowRationale
                                )
                            )

                        },

                        )
                    Spacer(Modifier.padding(vertical = 10.dp))

                    TodoCard(
                        todoState = homeState.todoState,
                        onConnect = {
                            component.onEvent(HomeEvent.ConnectTodoist)
                        },
                        onTodoOpen = { link ->
                            component.onEvent(HomeEvent.OnOpenLink(link))
                        }

                    )

                }
            }

    }

}






