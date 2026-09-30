package com.charan.yourday.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.charan.yourday.data.local.llm.datasource.LocalLlmDataSource
import com.charan.yourday.data.model.AIModelInfo
import com.charan.yourday.data.model.AIModels
import com.charan.yourday.data.model.AIResponse
import com.charan.yourday.data.model.LlmDownloadStatus
import com.charan.yourday.data.model.LlmGenerationEvent
import com.charan.yourday.data.repository.LocalLLMRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single(binds = [LocalLLMRepository::class])
class LocalLLMRepositoryImpl(
    private val localLlmDataSource: LocalLlmDataSource,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dataStore: DataStore<Preferences>
) : LocalLLMRepository {

    companion object {
        private val AI_SUMMARY_KEY = stringPreferencesKey("ai_summary")
        private val SUMMARY_MAX_AGE_MILLIS = 3 * 60 * 60 * 1000L
        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    override val selectedModel: Flow<AIModelInfo> =
        userPreferencesRepository.selectedAIModelId.map(AIModels::findById)

    override val cachedSummary: Flow<AIResponse?> = dataStore.data.map { preferences ->
        preferences[AI_SUMMARY_KEY]?.let { jsonString ->
            runCatching {
                json.decodeFromString<AIResponse>(jsonString)
            }.getOrNull()
        }
    }

    override suspend fun isModelDownloaded(): Boolean =
        localLlmDataSource.isModelDownloaded(selectedModel.first())

    override fun downloadModel(): Flow<LlmDownloadStatus> = flow {
        emitAll(localLlmDataSource.downloadModel(selectedModel.first()))
    }

    override suspend fun deleteModel(): Result<Unit> = runCatching {
        localLlmDataSource.deleteModel(selectedModel.first())
    }.onSuccess {
        dataStore.edit { preferences ->
            preferences.remove(AI_SUMMARY_KEY)
        }
    }

    override fun generateDaySummary(
        input: String,
        forceRefresh: Boolean
    ): Flow<LlmGenerationEvent> = flow {
        val model = selectedModel.first()

        if (!forceRefresh) {
            cachedSummary.first()?.takeIf { it.isFresh() }?.let { cached ->
                emit(LlmGenerationEvent.Completed(cached))
                return@flow
            }
        }

        localLlmDataSource.generateSummary(model, input).collect { event ->
            if (event is LlmGenerationEvent.Completed) {
                val response = event.response.copy(
                    generatedAtEpochMillis = DateUtils.getCurrentTimeInMillis()
                )
                dataStore.edit { preferences ->
                    preferences[AI_SUMMARY_KEY] = json.encodeToString(response)
                }
                emit(LlmGenerationEvent.Completed(response))
            } else {
                emit(event)
            }
        }
    }

    private fun AIResponse.isFresh(): Boolean {
        val generatedAt = generatedAtEpochMillis ?: return false
        val age = DateUtils.getCurrentTimeInMillis() - generatedAt
        return age in 0..SUMMARY_MAX_AGE_MILLIS
    }
}
