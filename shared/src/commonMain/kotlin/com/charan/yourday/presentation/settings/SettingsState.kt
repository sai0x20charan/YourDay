package com.charan.yourday.presentation.settings

import com.charan.yourday.presentation.common.DropDownItem
import com.charan.yourday.utils.WeatherUnits

data class SettingsState(
    val isTodoistConnected: Boolean? = null,
    val weatherUnits: String? = null,
    val dropDownItems: List<DropDownItem> = listOf(
        DropDownItem(WeatherUnits.C),
        DropDownItem(WeatherUnits.F)
    ),
    val appVersion: String? = null,
    val showDropDown: Boolean = false
)
