package com.charan.yourday.data.model

import com.charan.yourday.MR
import com.charan.yourday.*
import com.charan.yourday.utils.TodoProvidersEnums
import dev.icerock.moko.resources.ImageResource

data class TodoProvider (
    val providerName : String,
    val providerLogo : ImageResource
)

val todoProvider = listOf<TodoProvider>(
    TodoProvider(providerName = TodoProvidersEnums.TODOIST.name, providerLogo = MR.images.Todoist),
)
