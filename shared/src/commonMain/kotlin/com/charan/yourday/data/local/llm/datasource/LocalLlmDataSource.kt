package com.charan.yourday.data.local.llm.datasource

import com.charan.yourday.data.model.AIModelInfo
import com.charan.yourday.data.model.AIResponse
import com.charan.yourday.data.model.LlmDownloadStatus
import com.charan.yourday.data.model.LlmGenerationEvent
import kotlinx.coroutines.flow.Flow

interface LocalLlmDataSource {

    suspend fun init()
    suspend fun registerModel(model: AIModelInfo)
    fun downloadModel(model: AIModelInfo): Flow<LlmDownloadStatus>
    suspend fun isModelDownloaded(model: AIModelInfo): Boolean
    fun generateSummary(model: AIModelInfo, input: String): Flow<LlmGenerationEvent>
    suspend fun deleteModel(model: AIModelInfo)
}
