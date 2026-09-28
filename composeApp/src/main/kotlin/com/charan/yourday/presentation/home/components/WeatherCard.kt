package com.charan.yourday.presentation.home.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.presentation.home.ForecastWeatherState
import com.charan.yourday.utils.DateUtils.toTimeString
import com.charan.yourday.utils.WeatherUnitsEnums
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
    weatherConditionText : String,
    onLocationPermissionAccess: () -> Unit,
    weatherUnits : String,
    forecastData: List<ForecastWeatherState>
) {
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
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
                            Text(
                                text = "${currentTemperature.orEmpty()}° ${weatherUnits} ",
                                style = MaterialTheme.typography.headlineMediumEmphasized,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = weatherConditionText,
                                style = MaterialTheme.typography.labelMediumEmphasized,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )


                        }

                        Image(
                            painter = painterResource(
                                currentWeatherIcon ?: MR.images.cloudy
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    if (forecastData.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(forecastData) { item ->
                                CompactForecastChip(item,weatherUnits)
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
    weatherUnits : String
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            item.time.orEmpty(),
            style = MaterialTheme.typography.labelSmallEmphasized,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Image(
            painter = painterResource(
                item.icon ?: MR.images.cloudy
            ),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
            Text(
                text = "${item.temp.toString()}° $weatherUnits ",
                style = MaterialTheme.typography.labelMediumEmphasized,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

    }

}
