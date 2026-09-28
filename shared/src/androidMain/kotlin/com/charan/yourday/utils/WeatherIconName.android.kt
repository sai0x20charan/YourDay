package com.charan.yourday.utils

import android.util.Log
import com.charan.yourday.MR
import dev.icerock.moko.resources.ImageResource

actual object WeatherIconName {
    actual fun weatherIcon(code: Int,isDay : Boolean): ImageResource? {
        val iconName = weatherIconsMap.entries.find { it.key == code }?.value
        return if(isDay) iconName?.first else iconName?.second
    }
}

private val weatherIconsMap = mapOf(
    1000 to Pair(MR.images.clear_day, MR.images.clear_night),
    1003 to Pair(MR.images.partly_cloudy_day, MR.images.partly_cloudy_night),
    1006 to Pair(MR.images.cloudy, MR.images.cloudy),
    1009 to Pair(MR.images.cloudy, MR.images.cloudy),
    1030 to Pair(MR.images.haze_fog_dust_smoke, MR.images.haze_fog_dust_smoke),
    1063 to Pair(MR.images.cloudy_with_rain_light, MR.images.cloudy_with_rain_dark),
    1066 to Pair(MR.images.cloudy_with_snow_light, MR.images.cloudy_with_snow_dark),
    1069 to Pair(MR.images.cloudy_with_sunny_light, MR.images.cloudy_with_sunny_dark),
    1072 to Pair(MR.images.cloudy_with_sunny_light, MR.images.cloudy_with_sunny_dark),
    1087 to Pair(MR.images.isolated_scattered_thunderstorms_day, MR.images.isolated_scattered_thunderstorms_night),
    1114 to Pair(MR.images.blowing_snow, MR.images.blowing_snow),
    1117 to Pair(MR.images.blizzard, MR.images.blizzard),
    1135 to Pair(MR.images.haze_fog_dust_smoke, MR.images.haze_fog_dust_smoke),
    1147 to Pair(MR.images.haze_fog_dust_smoke, MR.images.haze_fog_dust_smoke),
    1150 to Pair(MR.images.drizzle, MR.images.drizzle),
    1153 to Pair(MR.images.drizzle, MR.images.drizzle),
    1168 to Pair(MR.images.icy, MR.images.icy),
    1171 to Pair(MR.images.icy, MR.images.icy),
    1180 to Pair(MR.images.rain_with_sunny_light, MR.images.rain_with_sunny_dark),
    1183 to Pair(MR.images.rain_with_cloudy_light, MR.images.rain_with_cloudy_dark),
    1186 to Pair(MR.images.rain_with_sunny_light, MR.images.rain_with_sunny_dark),
    1189 to Pair(MR.images.rain_with_cloudy_light, MR.images.rain_with_cloudy_dark),
    1192 to Pair(MR.images.rain_with_cloudy_light, MR.images.rain_with_cloudy_dark),
    1195 to Pair(MR.images.rain_with_cloudy_light, MR.images.rain_with_cloudy_dark),
    1198 to Pair(MR.images.icy, MR.images.icy),
    1201 to Pair(MR.images.icy, MR.images.icy),
    1204 to Pair(MR.images.sleet_hail, MR.images.sleet_hail),
    1207 to Pair(MR.images.sleet_hail, MR.images.sleet_hail),
    1210 to Pair(MR.images.scattered_snow_showers_day, MR.images.scattered_snow_showers_night),
    1213 to Pair(MR.images.scattered_snow_showers_day, MR.images.scattered_snow_showers_night),
    1216 to Pair(MR.images.cloudy_with_snow_light, MR.images.cloudy_with_snow_dark),
    1219 to Pair(MR.images.cloudy_with_snow_light, MR.images.cloudy_with_snow_dark),
    1222 to Pair(MR.images.cloudy_with_sunny_light, MR.images.cloudy_with_sunny_dark),
    1225 to Pair(MR.images.cloudy_with_sunny_light, MR.images.cloudy_with_sunny_dark),
    1237 to Pair(MR.images.icy, MR.images.icy),
    1240 to Pair(MR.images.showers_rain, MR.images.showers_rain),
    1243 to Pair(MR.images.showers_rain, MR.images.showers_rain),
    1246 to Pair(MR.images.showers_rain, MR.images.showers_rain),
    1249 to Pair(MR.images.sleet_hail, MR.images.sleet_hail),
    1252 to Pair(MR.images.sleet_hail, MR.images.sleet_hail),
    1255 to Pair(MR.images.showers_snow, MR.images.showers_snow),
    1258 to Pair(MR.images.showers_snow, MR.images.showers_snow),
    1261 to Pair(MR.images.sleet_hail, MR.images.sleet_hail),
    1264 to Pair(MR.images.sleet_hail, MR.images.sleet_hail),
    1273 to Pair(MR.images.isolated_thunderstorms, MR.images.isolated_thunderstorms),
    1276 to Pair(MR.images.strong_thunderstorms, MR.images.strong_thunderstorms),
    1279 to Pair(MR.images.strong_thunderstorms, MR.images.strong_thunderstorms),
    1282 to Pair(MR.images.strong_thunderstorms, MR.images.strong_thunderstorms),
    1300 to Pair(MR.images.sunny_with_cloudy_light, MR.images.sunny_with_cloudy_dark),
    1303 to Pair(MR.images.sunny_with_rain_light, MR.images.sunny_with_rain_dark),
    1306 to Pair(MR.images.sunny_with_snow_light, MR.images.sunny_with_snow_dark),
    1310 to Pair(MR.images.tornado, MR.images.tornado),
    1313 to Pair(MR.images.tropical_storm_hurricane, MR.images.tropical_storm_hurricane),
    1316 to Pair(MR.images.umbrella, MR.images.umbrella),
    1320 to Pair(MR.images.very_cold, MR.images.very_cold),
    1322 to Pair(MR.images.very_hot, MR.images.very_hot)
)
