package com.charan.yourday.utils

import dev.icerock.moko.resources.ImageResource

expect object WeatherIconName {
    fun weatherIcon(code : Int,isDay : Boolean) : ImageResource?
}
