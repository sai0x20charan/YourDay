package com.charan.yourday.data.repository.impl

import com.charan.yourday.data.model.AIResponse
import com.charan.yourday.data.repository.LocalLLMRepository
import com.charan.yourday.utils.ProcessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class LocalLLMRepositoryImpl : LocalLLMRepository {
    override suspend fun initModel(modelName: String) {}

    override fun downloadModel(modelName: String): Flow<ProcessState<Boolean>> =
        flowOf(ProcessState.Error("Local LLM is not supported on iOS yet"))

    override suspend fun isModelDownloaded(modelName: String): Boolean = false

    override suspend fun generateDaySummary(
        modelName: String,
        input: String
    ): Flow<ProcessState<AIResponse>> =
        flowOf(ProcessState.Error("Local LLM is not supported on iOS yet"))

    override suspend fun deleteModel(modelName: String): Flow<ProcessState<Boolean>> =
        flowOf(ProcessState.Error("Local LLM is not supported on iOS yet"))
}
