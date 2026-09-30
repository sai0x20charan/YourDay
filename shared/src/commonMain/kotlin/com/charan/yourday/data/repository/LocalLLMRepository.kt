package com.charan.yourday.data.repository

import com.charan.yourday.data.model.AIModelInfo
import com.charan.yourday.data.model.AIResponse
import com.charan.yourday.data.model.LlmDownloadStatus
import com.charan.yourday.data.model.LlmGenerationEvent
import kotlinx.coroutines.flow.Flow

interface LocalLLMRepository {

    val selectedModel: Flow<AIModelInfo>

    /**
     * Last generated summary, persisted locally so the app renders instantly and offline.
     * Emits `null` when nothing has been generated yet.
     */
    val cachedSummary: Flow<AIResponse?>

    suspend fun isModelDownloaded(): Boolean

    fun downloadModel(): Flow<LlmDownloadStatus>

    suspend fun deleteModel(): Result<Unit>

    /**
     * Emits the cached summary immediately when a fresh one exists, otherwise runs the model.
     * Use [forceRefresh] to always regenerate, ignoring the cache.
     */
    fun generateDaySummary(
        input: String,
        forceRefresh: Boolean = false
    ): Flow<LlmGenerationEvent>
}
