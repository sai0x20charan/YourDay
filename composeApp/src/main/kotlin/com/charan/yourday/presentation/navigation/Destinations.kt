package com.charan.yourday.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data class HomeScreenNav(
    val code : String? = null,
    val state : String? = null,
    val error : String? = null
)