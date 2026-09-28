package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charan.yourday.MR
import com.charan.yourday.presentation.home.ForecastWeatherState
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherCard(
    isLoading: Boolean,
    error: String?,
    hasContent: Boolean,
    location: String?,
    currentWeatherIcon: ImageResource?,
    currentTemperature: String?,
    isPermissionGranted: Boolean,
    weatherConditionText: String,
    onLocationPermissionAccess: () -> Unit,
    weatherUnits: String,
    forecastData: List<ForecastWeatherState>,
    scrollToCurrentTimeIndex: Int
) {
    val lazyScrollState = rememberLazyListState()

    LaunchedEffect(scrollToCurrentTimeIndex) {
        if (scrollToCurrentTimeIndex != 0) {
            lazyScrollState.animateScrollToItem(scrollToCurrentTimeIndex)
        }
    }

    ContentElevatedCard(
        isLoading = isLoading,
        hasError = error,
        content = {
            if (!isPermissionGranted) {
                GrantPermissionContent(
                    title = "Enable location to show weather"
                ) {
                    onLocationPermissionAccess()
                }
                return@ContentElevatedCard
            }

            if (hasContent) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = location.orEmpty(),
                                    style = MaterialTheme.typography.labelMediumEmphasized,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "${currentTemperature.orEmpty()}°$weatherUnits",
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 56.sp
                            )

                            Text(
                                text = weatherConditionText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Image(
                            painter = painterResource(
                                currentWeatherIcon ?: MR.images.cloudy
                            ),
                            contentDescription = weatherConditionText,
                            modifier = Modifier.size(72.dp)
                        )
                    }

                    if (forecastData.isNotEmpty()) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 0.5.dp
                        )

                        LazyRow(
                            state = lazyScrollState,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            items(forecastData) { item ->
                                CompactForecastChip(item, weatherUnits)
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CompactForecastChip(
    item: ForecastWeatherState,
    weatherUnits: String,
) {

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Text(
                text = item.time.orEmpty(),
                style = MaterialTheme.typography.labelSmallEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )


            Image(
                painter = painterResource(item.icon ?: MR.images.cloudy),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )

            Text(
                text = "${item.temp}°$weatherUnits",
                style = MaterialTheme.typography.labelMediumEmphasized,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}