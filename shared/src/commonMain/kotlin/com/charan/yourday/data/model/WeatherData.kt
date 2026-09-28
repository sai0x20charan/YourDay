package com.charan.yourday.data.model

import dev.icerock.moko.resources.ImageResource
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
    val tempC : Int? = null,
    val tempF : Int? = null,
    val maxTempC : Int? = null,
    val maxTempF : Int? = null,
    val minTempC : Int? = null,
    val minTempF : Int? = null,
    val currentCondition : String? = null,
     val temperatureIcon : Int? = null,
    val time : LocalDateTime? = null,
    val location : String? = null,
    val forecast : List<WeatherData>? = null,
    val isDay : Boolean? = null
)
