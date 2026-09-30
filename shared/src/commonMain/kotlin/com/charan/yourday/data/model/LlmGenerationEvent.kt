package com.charan.yourday.data.model

sealed interface LlmGenerationEvent {
    data class Streaming(val response: AIResponse) : LlmGenerationEvent
    data class Completed(val response: AIResponse) : LlmGenerationEvent
    data class Failed(val message: String) : LlmGenerationEvent
}
