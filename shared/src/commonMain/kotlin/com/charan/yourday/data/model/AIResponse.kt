package com.charan.yourday.data.model

data class AIResponse(
    val modelName : String,
    val thinkingResponse : String? = null,
    val aiResponse : String = "",
    val isThinking : Boolean = false,
)
