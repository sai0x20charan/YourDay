package com.charan.yourday.presentation.onBoarding

import com.charan.yourday.MR
import dev.icerock.moko.resources.ImageResource

data class OnBoardPageModel(
    val title : String,
    var image : ImageResource,
    val description : String
)

val onBoardModelList = listOf(
    OnBoardPageModel(
        title = "Welcome to Yourday",
        image = MR.images.icy,
        description = "Your all-in-one companion for planning and organizing your day with weather updates, calendar events, and to-do lists."
    ),
    OnBoardPageModel(
        title = "Real-time Weather.",
        image = MR.images.sleet_hail,
        description = "Stay prepared with accurate weather forecasts. Get daily updates and plan your activities accordingly."
    )


)
