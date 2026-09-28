package com.charan.yourday.presentation.onboarding

import android.Manifest
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.presentation.home.HomeEvent
import com.charan.yourday.presentation.home.HomeScreenComponent
import com.charan.yourday.presentation.home.HomeViewEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnBoardingScreen(
    component: HomeScreenComponent
) {
    val state by component.state.collectAsState()

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    Scaffold (
        modifier = Modifier.navigationBarsPadding(),
        bottomBar = {
            FilledTonalButton(
                onClick = { component.onEvent(HomeEvent.OnBoardingFinish) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                shapes = ButtonDefaults.shapes(shape = MaterialTheme.shapes.extraLarge)
            ) {
                Text(
                    "Get Started",
                    style = MaterialTheme.typography.titleMediumEmphasized
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

    ){ padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, delayMillis = 200)) +
                            slideInVertically(tween(600, delayMillis = 200)) { it / 2 }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Welcome to Your Day",
                            style = MaterialTheme.typography.headlineLargeEmphasized,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Your companion for planning your day with current weather conditions, events, and tasks.",
                            style = MaterialTheme.typography.bodyMediumEmphasized,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(40.dp))

                AnimatedFeatureCard(
                    visible = visible,
                    delay = 300,
                    icon = Icons.Default.WbSunny,
                    title = "Weather Insights",
                    description = "Real-time weather updates to plan your day",
                    isGranted = state.weatherState.isLocationPermissionGranted,
                    grantedText = "Location access granted",
                    buttonText = "Enable Location",
                    onAction = {
                        component.onEvent(
                            HomeEvent.RequestLocationPermission(
                               false
                            )
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                AnimatedFeatureCard(
                    visible = visible,
                    delay = 450,
                    icon = Icons.Default.CalendarMonth,
                    title = "Calendar Sync",
                    description = "Never miss important events and meetings",
                    isGranted = state.calenderData.isCalenderPermissionGranted,
                    grantedText = "Calendar access granted",
                    buttonText = "Grant Calendar Access",
                    onAction = {
                        component.onEvent(
                            HomeEvent.RequestCalendarPermission(
                                false
                            )
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                AnimatedTodoCard(
                    visible = visible,
                    delay = 600,
                    isTodoConnected = state.todoState.isTodoAuthenticated,
                    onTodoistConnect = {
                        component.onEvent(HomeEvent.ConnectTodoist)
                    }
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnimatedFeatureCard(
    visible: Boolean,
    delay: Int,
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    grantedText: String,
    buttonText: String,
    onAction: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600, delayMillis = delay)) +
                slideInHorizontally(tween(600, delayMillis = delay)) { it / 2 }
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                AnimatedContent(
                    targetState = isGranted,
                    transitionSpec = {
                        (fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.9f))
                            .togetherWith(fadeOut(tween(200)) + scaleOut(tween(200)))
                    },
                    label = "permission_state"
                ) { isGrantedState ->
                    if (isGrantedState) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .padding(12.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                grantedText,
                                style = MaterialTheme.typography.bodyMediumEmphasized,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    } else {
                        FilledTonalButton(
                            onClick = onAction,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(buttonText)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnimatedTodoCard(
    visible: Boolean,
    delay: Int,
    isTodoConnected: Boolean,
    onTodoistConnect: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600, delayMillis = delay)) +
                slideInHorizontally(tween(600, delayMillis = delay)) { it / 2 }
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Task Management",
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Integrate Todoist and see all your daily tasks in one place.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                AnimatedContent(
                    targetState = isTodoConnected,
                    transitionSpec = {
                        (fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.9f))
                            .togetherWith(fadeOut(tween(200)) + scaleOut(tween(200)))
                    },
                    label = "todoist_connection"
                ) { isConnected ->
                    if (isConnected) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .padding(12.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Image(
                                painter = painterResource(MR.images.Todoist),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                "Connected to Todoist",
                                style = MaterialTheme.typography.bodyMediumEmphasized,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f)
                            )

                        }
                    } else {
                        FilledTonalButton(
                            onClick = onTodoistConnect,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Image(
                                painter = painterResource(MR.images.Todoist),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Connect Todoist")
                        }
                    }
                }
            }
        }
    }
}