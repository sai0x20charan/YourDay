package com.charan.yourday.data.mapper

import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.data.network.responseDTO.ForecastClass
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.utils.DateUtils
import com.charan.yourday.utils.DateUtils.toLocalDateTime
import com.charan.yourday.utils.WeatherIconName
import com.charan.yourday.utils.WeatherUnits
import kotlinx.datetime.TimeZone

fun WeatherDTO.toWeatherData()  : WeatherData {
    return WeatherData(
        tempC = this.current?.tempC?.toInt(),
        tempF = this.current?.tempF?.toInt(),
        maxTempC = this.forecast?.forecastday?.first()?.day?.maxtempC?.toInt(),
        maxTempF = this.forecast?.forecastday?.first()?.day?.maxtempF?.toInt(),
        minTempC = this.forecast?.forecastday?.first()?.day?.mintempC?.toInt(),
        minTempF = this.forecast?.forecastday?.first()?.day?.mintempF?.toInt(),
        currentCondition = this.current?.condition?.text,
        temperatureIcon = this.current?.condition?.code?.toInt(),
        isDay = this.current?.isDay == 1.0,
        forecast = this.forecast?.toWeatherData(TimeZone.of(this?.location?.tzID ?: "UTC")),
        location = this.location?.name.toString()
    )

}

fun ForecastClass.toWeatherData(timeZone: TimeZone) : List<WeatherData>?{
    val forecastWeather = this.forecastday?.firstOrNull()?.hour
    val weatherData = forecastWeather?.map {
            WeatherData(
               tempC = it.tempC?.toInt(),
                tempF = it.tempF?.toInt(),
                maxTempC = null,
                maxTempF = null,
                minTempC = null,
                minTempF = null,
                currentCondition = it.condition?.text,
                temperatureIcon = it.condition?.code?.toInt(),
                isDay = it.isDay == 1.0,
                time = (it.timeEpoch?.times(1000))?.toLocalDateTime(timeZone)
            )
    }
    return weatherData


}