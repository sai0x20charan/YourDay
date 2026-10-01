package com.charan.yourday.data.local.llm.datasource

import android.content.Context
import android.util.Log
import com.charan.yourday.data.model.AIModelInfo
import com.charan.yourday.data.model.AIResponse
import com.charan.yourday.data.model.LlmDownloadStage
import com.charan.yourday.data.model.LlmDownloadStatus
import com.charan.yourday.data.model.LlmGenerationEvent
import com.runanywhere.sdk.llm.llamacpp.LlamaCPP
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.api.DownloadEvent
import com.runanywhere.sdk.public.api.GenerationEvent
import com.runanywhere.sdk.public.api.InferenceFramework
import com.runanywhere.sdk.public.api.LlmOptions
import com.runanywhere.sdk.public.api.ModelCategory
import com.runanywhere.sdk.public.api.ModelRegistration
import com.runanywhere.sdk.public.api.llm
import com.runanywhere.sdk.public.api.models
import com.runanywhere.sdk.public.extensions.Models.isDownloadedOnDisk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn

class RunAnywhereLlmDataSource(
    private val context: Context
) : LocalLlmDataSource {

    companion object {
        private const val TAG = "RunAnywhereLlmDataSource"
        private const val MAX_OUTPUT_TOKENS = 2048
        private const val TEMPERATURE = 0.2f
        private const val THINK_OPEN_TAG = "<think>"
        private const val THINK_END_TAG = "</think>"
    }

    override suspend fun init() {
        try {
            LlamaCPP.register()
        } catch (e: Exception) {
            Log.w(TAG, "LlamaCPP.register failed: ${e.message}")
        }
        if (!RunAnywhere.isInitialized) {
            RunAnywhere.initialize(context = context)
        }
    }

    override suspend fun registerModel(model: AIModelInfo) {
        RunAnywhere.models.register(
            ModelRegistration.url(
                id = model.id,
                name = model.name,
                url = model.url,
                framework = InferenceFramework.INFERENCE_FRAMEWORK_LLAMA_CPP,
                category = ModelCategory.MODEL_CATEGORY_LANGUAGE,
                memoryBytes = model.memoryBytes,
                downloadBytes = model.downloadBytes,
                supportsThinking = model.supportsThinking
            )
        )
    }

    override fun downloadModel(model: AIModelInfo): Flow<LlmDownloadStatus> = callbackFlow {
        registerModel(model)
        RunAnywhere.models.download(model.id).collectLatest { event ->
            val status = when (event) {
                is DownloadEvent.Cancelled -> LlmDownloadStatus(
                    stage = LlmDownloadStage.DOWNLOADING,
                    errorMessage = "Download cancelled"
                )

                is DownloadEvent.Completed -> LlmDownloadStatus(
                    stage = LlmDownloadStage.COMPLETED,
                    progress = 1f
                )

                is DownloadEvent.Extracting -> LlmDownloadStatus(
                    stage = LlmDownloadStage.EXTRACTING
                )

                is DownloadEvent.Failed -> LlmDownloadStatus(
                    stage = LlmDownloadStage.DOWNLOADING,
                    errorMessage = "Download failed: ${event.error.message}"
                )

                is DownloadEvent.Progress -> LlmDownloadStatus(
                    stage = LlmDownloadStage.DOWNLOADING,
                    progress = event.overallProgress ?: event.fraction
                )

                is DownloadEvent.Started -> LlmDownloadStatus(
                    stage = LlmDownloadStage.DOWNLOADING,
                    progress = 0f
                )

                is DownloadEvent.Verifying -> LlmDownloadStatus(
                    stage = LlmDownloadStage.VERIFYING
                )
            }
            trySend(status)
            if (status.isDownloaded || status.isFailed) {
                close()
            }
        }
        awaitClose { this.cancel() }
    }.flowOn(Dispatchers.IO)

    override suspend fun isModelDownloaded(model: AIModelInfo): Boolean {
        val registered = RunAnywhere.models.get(model.id) ?: return false
        return registered.isDownloadedOnDisk
    }

    override fun generateSummary(
        model: AIModelInfo,
        input: String
    ): Flow<LlmGenerationEvent> = channelFlow {
        try {

            val options = LlmOptions(
                model = model.id,
                maxOutputTokens = MAX_OUTPUT_TOKENS,
                temperature = TEMPERATURE
            )
            val accumulated = StringBuilder()
            val thinkingAccumulated = StringBuilder()

            RunAnywhere.llm.generateStream(input, options).collectLatest { event ->
                when (event) {
                    is GenerationEvent.Cancelled -> {
                        send(LlmGenerationEvent.Failed("Generation cancelled"))
                    }

                    is GenerationEvent.Completed -> {

                        send(
                            LlmGenerationEvent.Completed(
                                AIResponse(
                                    modelName = model.id,
                                    aiResponse = event.result.text,
                                    thinkingResponse = event.result.thinkingText
                                )
                            )
                        )
                    }

                    is GenerationEvent.Failed -> {
                        send(LlmGenerationEvent.Failed("Generation failed: ${event.error.message}"))
                    }

                    is GenerationEvent.ReasoningDelta -> {
                        thinkingAccumulated.append(event.text)

                        send(
                            LlmGenerationEvent.Streaming(
                                AIResponse(
                                    modelName = model.id,
                                    aiResponse = "",
                                    thinkingResponse = thinkingAccumulated.toString(),
                                    isThinking = true
                                )
                            )
                        )
                    }

                    is GenerationEvent.TextDelta -> {
                        accumulated.append(event.text)
                        send(
                            LlmGenerationEvent.Streaming(
                                AIResponse(
                                    modelName = model.id,
                                    aiResponse = accumulated.toString(),
                                    thinkingResponse = thinkingAccumulated.toString().ifEmpty { null },
                                    isThinking = false
                                )
                            )
                        )
                    }

                    is GenerationEvent.OutputItemAdded,
                    is GenerationEvent.Started,
                    is GenerationEvent.ToolArgumentsDelta,
                    is GenerationEvent.ToolArgumentsDone,
                    is GenerationEvent.ToolCallAdded,
                    is GenerationEvent.Usage -> Unit
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating summary: ${e.message}", e)
            send(LlmGenerationEvent.Failed(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun deleteModel(model: AIModelInfo) {
        registerModel(model)
        checkNotNull(RunAnywhere.models.get(model.id)) { "Model not found" }

        RunAnywhere.models.delete(model.id)
    }

    private fun extractContent(text: String, thinking: String?): Pair<String, String?> {
        val rawText = text.trim()
        val rawThinking = thinking?.trim().orEmpty()

        if (rawText.isNotEmpty()) {
            return Pair(rawText, rawThinking.ifEmpty { null })
        }

        return Pair(
            rawThinking.replace(THINK_OPEN_TAG, "").replace(THINK_END_TAG, "").trim(),
            null
        )
    }
}
