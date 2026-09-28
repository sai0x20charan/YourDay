package com.charan.yourday.data.repository

import com.charan.yourday.data.model.AIResponse
import com.charan.yourday.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface LocalLLMRepository {

    suspend fun initModel(modelName: String = "lfm2.5-230m-q4_k_m")

    fun downloadModel(modelName: String = "lfm2.5-230m-q4_k_m"): Flow<ProcessState<Boolean>>

    suspend fun isModelDownloaded(modelName: String = "lfm2.5-230m-q4_k_m"): Boolean

    suspend fun generateDaySummary(
        modelName: String = "lfm2.5-230m-q4_k_m",
        input: String
    ): Flow<ProcessState<AIResponse>>

    suspend fun deleteModel(modelName: String = "lfm2.5-230m-q4_k_m"): Flow<ProcessState<Boolean>>
}
