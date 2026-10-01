package com.charan.yourday.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AIModelInfo(
    val id: String,
    val name: String,
    val url: String,
    val memoryBytes: Long,
    val downloadBytes: Long,
    val supportsThinking: Boolean
)

object AIModels {
    const val DEFAULT_MODEL_ID = "qwen3.5-0.8b-q4_k_m"

    val AVAILABLE_MODELS: List<AIModelInfo> = listOf(
        AIModelInfo(
            id = "lfm2.5-230m-q4_k_m",
            name = "LFM2.5 230M Q4_K_M",
            url = "https://huggingface.co/LiquidAI/LFM2.5-230M-GGUF/resolve/main/LFM2.5-230M-Q4_K_M.gguf",
            memoryBytes = 350_000_000L,
            downloadBytes = 160_512_800L,
            supportsThinking = false
        ),
        AIModelInfo(
            id = "qwen3.5-0.8b-q4_k_m",
            name = "Qwen3.5 0.8B Q4_K_M",
            url = "https://huggingface.co/unsloth/Qwen3.5-0.8B-GGUF/resolve/main/Qwen3.5-0.8B-Q4_K_M.gguf",
            memoryBytes = 900_000_000L,
            downloadBytes = 532_517_120L,
            supportsThinking = true
        ),
        AIModelInfo(
            id = "qwen3.5-2b-q4_k_m",
            name = "Qwen3.5 2B Q4_K_M",
            url = "https://huggingface.co/unsloth/Qwen3.5-2B-GGUF/resolve/main/Qwen3.5-2B-Q4_K_M.gguf",
            memoryBytes = 2_200_000_000L,
            downloadBytes = 1_430_000_000L,
            supportsThinking = true
        )
    )

    fun findById(id: String): AIModelInfo =
        AVAILABLE_MODELS.find { it.id == id } ?: AVAILABLE_MODELS.first()
}
