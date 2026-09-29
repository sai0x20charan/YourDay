package com.charan.yourday.presentation.home

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
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularWavyProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.charan.yourday.presentation.common.CustomDropDown
import com.charan.yourday.presentation.common.DropDownItem
import com.charan.yourday.presentation.home.components.AIResponseCard
import com.charan.yourday.presentation.home.components.CalendarCard
import com.charan.yourday.presentation.home.components.TodoCard
import com.charan.yourday.presentation.home.components.WeatherCard
import kotlinx.coroutines.flow.collectLatest

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun HomeScreen(
    component: HomeScreenComponent,
) {
    val state by component.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(component.effects) {
        component.effects.collectLatest {
            when (it) {
                is HomeEffect.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val scroll = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pullToRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { component.onEvent(HomeEvent.RefreshData) },
    )
    val listState = rememberLazyListState()
    val isThresholdReached by remember {
        derivedStateOf {
            pullToRefreshState.progress.dp >= 1.dp
        }
    }
    val isPulledDown by remember {
        derivedStateOf {
            pullToRefreshState.progress.dp > 0.dp
        }
    }
    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(state.greetings)
                },
                subtitle = {
                    Text(state.currentDateTime)
                },
                scrollBehavior = scroll,
                actions = {
                    IconButton(
                        onClick = {
                            component.onEvent(HomeEvent.ShowDropdownMenu(true))
                        },
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    CustomDropDown(
                        items = dropdownItems,
                        onItemSelected = { _, index ->
                            when (index) {
                                0 -> component.onEvent(HomeEvent.OpenSettingsPage)
                                1 -> component.onEvent(HomeEvent.RefreshData)
                            }
                        },
                        isExpanded = state.showDropDown,
                        onDismiss = {
                            component.onEvent(HomeEvent.ShowDropdownMenu(false))
                        }
                    )
                }
            )
        },
        modifier = Modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullToRefreshState)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scroll.nestedScrollConnection),
                state = listState,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    AnimatedVisibility(
                        visible = isPulledDown,
                        enter = expandVertically() + scaleIn(),
                        exit = shrinkVertically() + scaleOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .offset(y = 20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularWavyProgressIndicator(
                                modifier = Modifier
                                    .size(40.dp)
                                    .animateContentSize(),
                                progress = {
                                    if (isThresholdReached) 1f else pullToRefreshState.progress.coerceIn(0f, 1f)
                                },
                            )
                        }
                    }
                }
                item {
                    if (state.aiResponseState.isModelDownloaded) {
                        AIResponseCard(
                            thinkingResponse = state.aiResponseState.thinkingResponse ?: "",
                            aiResponse = state.aiResponseState.aiResponse ?: "",
                            isGenerating = state.aiResponseState.isGenerating,
                            showThinkingText = state.aiResponseState.showThinkingResponse,
                            onToggleThinking = {
                                component.onEvent(HomeEvent.OnToggleThinkingResponse)
                            },
                            isThinking = state.aiResponseState.isThinking
                        )
                        Spacer(Modifier.padding(vertical = 10.dp))
                    }

                    WeatherCard(
                        weatherState = state.weatherState,
                        grantPermission = {
                            component.onEvent(
                                HomeEvent.RequestLocationPermission
                            )
                        },
                        scrollToCurrentTimeIndex = state.weatherState.scrollToForecastCurrentTimeIndex
                    )
                    Spacer(Modifier.padding(vertical = 10.dp))
                    CalendarCard(
                        calendarState = state.calendarData,
                        grantPermission = {
                            component.onEvent(
                                HomeEvent.RequestCalendarPermission
                            )
                        },
                    )
                    Spacer(Modifier.padding(vertical = 10.dp))

                    TodoCard(
                        todoState = state.todoState,
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
}

private val dropdownItems = listOf(
    DropDownItem("Settings", Icons.Rounded.Settings),
    DropDownItem("Refresh", Icons.Rounded.Refresh),
)
