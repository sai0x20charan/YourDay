package com.charan.yourday.data.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TodoData(
    val id : String,
    val tasks : String?= null,
    val todoProvider : String? = null,
    val todoProviderLogo : Int? = null,
    val date : LocalDateTime? = null,
    val taskLink : String? = null,
    val isOverDue : Boolean? = null
)
