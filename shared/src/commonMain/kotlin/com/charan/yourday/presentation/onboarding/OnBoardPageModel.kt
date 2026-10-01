package com.charan.yourday.presentation.onboarding

import com.charan.yourday.*
import dev.icerock.moko.resources.ImageResource

data class OnBoardPageModel(
    val title : String,
    var image : ImageResource,
    val description : String
)
