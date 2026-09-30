package com.charan.yourday.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AIResponse(
    val modelName: String = "",
    val thinkingResponse: String? = null,
    val aiResponse: String = "",
    val isThinking: Boolean = false,
    val generatedAtEpochMillis: Long? = null
)
