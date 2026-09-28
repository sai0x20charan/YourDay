package com.charan.yourday.data.network.responseDTO

import com.charan.yourday.utils.UserPreferencesStore
import com.charan.yourday.utils.WeatherIconName
import dev.icerock.moko.resources.ImageResource
import kotlinx.serialization.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Serializable
data class WeatherDTO (
    val location: Location? = null,
    val current: Current? = null,
    val forecast: ForecastClass? = null
)

@Serializable
data class Current (
    @SerialName("last_updated_epoch")
    val lastUpdatedEpoch: Double? = null,

    @SerialName("last_updated")
    val lastUpdated: String? = null,

    @SerialName("temp_c")
    val tempC: Double? = null,

    @SerialName("temp_f")
    val tempF: Double? = null,

    @SerialName("is_day")
    val isDay: Double? = null,

    val condition: Condition? = null,

    @SerialName("wind_mph")
    val windMph: Double? = null,

    @SerialName("wind_kph")
    val windKph: Double? = null,

    @SerialName("wind_degree")
    val windDegree: Double? = null,

    @SerialName("wind_dir")
    val windDir: String? = null,

    @SerialName("pressure_mb")
    val pressureMB: Double? = null,

    @SerialName("pressure_in")
    val pressureIn: Double? = null,

    @SerialName("precip_mm")
    val precipMm: Double? = null,

    @SerialName("precip_in")
    val precipIn: Double? = null,

    val humidity: Double? = null,
    val cloud: Double? = null,

    @SerialName("feelslike_c")
    val feelslikeC: Double? = null,

    @SerialName("feelslike_f")
    val feelslikeF: Double? = null,

    @SerialName("windchill_c")
    val windchillC: Double? = null,

    @SerialName("windchill_f")
    val windchillF: Double? = null,

    @SerialName("heatindex_c")
    val heatindexC: Double? = null,

    @SerialName("heatindex_f")
    val heatindexF: Double? = null,

    @SerialName("dewpoint_c")
    val dewpointC: Double? = null,

    @SerialName("dewpoint_f")
    val dewpointF: Double? = null,

    @SerialName("vis_km")
    val visKM: Double? = null,

    @SerialName("vis_miles")
    val visMiles: Double? = null,

    val uv: Double? = null,

    @SerialName("gust_mph")
    val gustMph: Double? = null,

    @SerialName("gust_kph")
    val gustKph: Double? = null,

    @SerialName("air_quality")
    val airQuality: Map<String, Double>? = null,

    @SerialName("time_epoch")
    val timeEpoch: Long? = null,

    val time: String? = null,

    @SerialName("snow_cm")
    val snowCM: Double? = null,

    @SerialName("will_it_rain")
    val willItRain: Double? = null,

    @SerialName("chance_of_rain")
    val chanceOfRain: Double? = null,

    @SerialName("will_it_snow")
    val willItSnow: Double? = null,

    @SerialName("chance_of_snow")
    val chanceOfSnow: Double? = null
)

@Serializable
data class Condition (
    val text: String? = null,
    val icon: String? = null,
    val code: Double? = null
)


@Serializable
data class ForecastClass (
    val forecastday: List<Forecastday>? = null
)

@Serializable
data class Forecastday (
    val date: String? = null,

    @SerialName("date_epoch")
    val dateEpoch: Double? = null,

    val day: Day? = null,
    val astro: Astro? = null,
    val hour: List<Current>? = null
)

@Serializable
data class Astro (
    val sunrise: String? = null,
    val sunset: String? = null,
    val moonrise: String? = null,
    val moonset: String? = null,

    @SerialName("moon_phase")
    val moonPhase: String? = null,

    @SerialName("moon_illumination")
    val moonIllumination: Double? = null,

    @SerialName("is_moon_up")
    val isMoonUp: Double? = null,

    @SerialName("is_sun_up")
    val isSunUp: Double? = null
)

@Serializable
data class Day (
    @SerialName("maxtemp_c")
    val maxtempC: Double? = null,

    @SerialName("maxtemp_f")
    val maxtempF: Double? = null,

    @SerialName("mintemp_c")
    val mintempC: Double? = null,

    @SerialName("mintemp_f")
    val mintempF: Double? = null,

    @SerialName("avgtemp_c")
    val avgtempC: Double? = null,

    @SerialName("avgtemp_f")
    val avgtempF: Double? = null,

    @SerialName("maxwind_mph")
    val maxwindMph: Double? = null,

    @SerialName("maxwind_kph")
    val maxwindKph: Double? = null,

    @SerialName("totalprecip_mm")
    val totalprecipMm: Double? = null,

    @SerialName("totalprecip_in")
    val totalprecipIn: Double? = null,

    @SerialName("totalsnow_cm")
    val totalsnowCM: Double? = null,

    @SerialName("avgvis_km")
    val avgvisKM: Double? = null,

    @SerialName("avgvis_miles")
    val avgvisMiles: Double? = null,

    val avghumidity: Double? = null,

    @SerialName("daily_will_it_rain")
    val dailyWillItRain: Double? = null,

    @SerialName("daily_chance_of_rain")
    val dailyChanceOfRain: Double? = null,

    @SerialName("daily_will_it_snow")
    val dailyWillItSnow: Double? = null,

    @SerialName("daily_chance_of_snow")
    val dailyChanceOfSnow: Double? = null,

    val condition: Condition? = null,
    val uv: Double? = null
)

@Serializable
data class Location (
    val name: String? = null,
    val region: String? = null,
    val country: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,

    @SerialName("tz_id")
    val tzID: String? = null,

    @SerialName("localtime_epoch")
    val localtimeEpoch: Double? = null,

    val localtime: String? = null
)

@Serializable
data class WeatherError (
    val error: ErrorClass? = null
)

@Serializable
data class ErrorClass (
    val code: Long? = null,
    val message: String? = null
)


