package com.charan.yourday.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TodoistTokenDTO (
    var access_token : String? = null,
    var token_type : String? = null
)
