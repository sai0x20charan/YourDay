package com.charan.yourday.utils

import com.charan.yourday.MR
import dev.icerock.moko.resources.ImageResource

enum class TodoProvidersEnums {
    TODOIST,
    UNKNOWN;


}

fun String.getProviderLogo() : ImageResource? {
    return when(this){
        TodoProvidersEnums.TODOIST.name -> MR.images.Todoist
        else -> null
    }
}