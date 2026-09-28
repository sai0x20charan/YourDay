package com.charan.yourday.data.repository.impl

import android.content.Context
import android.util.Log
import com.charan.yourday.data.model.AIResponse
import com.charan.yourday.data.repository.LocalLLMRepository
import com.charan.yourday.utils.ProcessState
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose

class LocalLLMRepositoryImpl(
    val context: Context
) : LocalLLMRepository {

    companion object {
        private const val TAG = "LocalAiDataSource"

        const val DEFAULT_MODEL_ID = "lfm2.5-230m-q4_k_m"

        private val KNOWN_MODELS = mapOf(
            "lfm2.5-230m-q4_k_m" to ModelDef(
                id = "lfm2.5-230m-q4_k_m",
                name = "LFM2.5 230M Q4_K_M",
                url = "https://huggingface.co/LiquidCloud-GGUF/LFM-2.5-230M-Instruct-GGUF/resolve/main/LFM-2.5-230M-Instruct-Q4_K_M.gguf",
                memoryBytes = 350_000_000L,
                downloadBytes = 160_512_800L,
                supportsThinking = false
            ),
            "qwen3.5-0.8b-q4_k_m" to ModelDef(
                id = "qwen3.5-0.8b-q4_k_m",
                name = "Qwen3.5 0.8B Q4_K_M",
                url = "https://huggingface.co/unsloth/Qwen3.5-0.8B-GGUF/resolve/main/Qwen3.5-0.8B-Q4_K_M.gguf",
                memoryBytes = 900_000_000L,
                downloadBytes = 532_517_120L,
                supportsThinking = true
            ),
            "qwen3.5-2b-q4_k_m" to ModelDef(
                id = "qwen3.5-2b-q4_k_m",
                name = "Qwen3.5 2B Q4_K_M",
                url = "https://huggingface.co/unsloth/Qwen3.5-2B-GGUF/resolve/main/Qwen3.5-2B-Q4_K_M.gguf",
                memoryBytes = 2_200_000_000L,
                downloadBytes = 1_430_000_000L,
                supportsThinking = true
            )
        )
    }

    private data class ModelDef(
        val id: String,
        val name: String,
        val url: String,
        val memoryBytes: Long,
        val downloadBytes: Long,
        val supportsThinking: Boolean
    )

    private fun resolveModelDef(modelName: String): ModelDef {
        return KNOWN_MODELS[modelName] ?: KNOWN_MODELS[DEFAULT_MODEL_ID]!!
    }

    override suspend fun initModel(modelName: String) {
        val def = resolveModelDef(modelName)
        try {
            LlamaCPP.register()
        } catch (e: Exception) {
            Log.w(TAG, "LlamaCPP.register failed: ${e.message}")
        }
        if (!RunAnywhere.isInitialized) {
            RunAnywhere.initialize(context = context)
        }
        RunAnywhere.models.register(
            ModelRegistration.url(
                id = def.id,
                name = def.name,
                url = def.url,
                framework = InferenceFramework.INFERENCE_FRAMEWORK_LLAMA_CPP,
                category = ModelCategory.MODEL_CATEGORY_LANGUAGE,
                memoryBytes = def.memoryBytes,
                downloadBytes = def.downloadBytes,
                supportsThinking = def.supportsThinking
            ),
        )
    }

    override fun downloadModel(modelName: String): Flow<ProcessState<Boolean>> = callbackFlow {
        val def = resolveModelDef(modelName)
        try {
            initModel(def.id)
        } catch (e: Exception) {
            trySend(ProcessState.Error("Init failed: ${e.message}"))
            close()
            return@callbackFlow
        }
        RunAnywhere.models.download(def.id).collectLatest { state ->
            when (state) {
                is DownloadEvent.Cancelled -> {
                    trySend(ProcessState.Error("Download cancelled"))
                }
                is DownloadEvent.Completed -> {
                    trySend(ProcessState.Success(true))
                }
                is DownloadEvent.Extracting -> {
                    trySend(ProcessState.Loading())
                }
                is DownloadEvent.Failed -> {
                    trySend(ProcessState.Error("Download failed: ${state.error.message}"))
                }
                is DownloadEvent.Progress -> {
                    val progress = state.overallProgress ?: state.fraction
                    trySend(ProcessState.Loading(progress))
                }
                is DownloadEvent.Started -> {
                    trySend(ProcessState.Loading())
                }
                is DownloadEvent.Verifying -> {
                    trySend(ProcessState.Loading())
                }
            }
        }
        awaitClose { this.cancel() }
    }

    override suspend fun isModelDownloaded(modelName: String): Boolean {
        val def = resolveModelDef(modelName)
        initModel(def.id)
        val model = RunAnywhere.models.get(def.id) ?: return false
        return model.isDownloadedOnDisk
    }

    override suspend fun generateDaySummary(
        modelName: String,
        input: String
    ): Flow<ProcessState<AIResponse>> {
        val def = resolveModelDef(modelName)
        println("Generating summary with model ${def.id} for input: $input")

        return channelFlow {
            send(ProcessState.Loading())
            try {
                initModel(def.id)

                val options = LlmOptions(
                    model = def.id,
                    maxOutputTokens = 2048,
                    temperature = 0.2f
                )
                val accumulated = StringBuilder()
                val thinkingAccumulated = StringBuilder()

                RunAnywhere.llm.generateStream(
                    input,
                    options
                ).collectLatest { state ->
                    when (state) {
                        is GenerationEvent.Cancelled -> {
                            send(ProcessState.Error("Generation cancelled"))
                        }

                        is GenerationEvent.Completed -> {
                            val rawText = state.result.text.ifEmpty { accumulated.toString() }
                            val rawThinking = state.result.thinkingText ?: thinkingAccumulated.toString().ifEmpty { null }
                            val (finalText, finalThinking) = extractContent(rawText, rawThinking)

                            println("LLM Generation Completed: finalText length=${finalText.length}, thinking length=${finalThinking?.length ?: 0}")

                            send(
                                ProcessState.Success(
                                    AIResponse(
                                        modelName = def.id,
                                        aiResponse = finalText,
                                        thinkingResponse = finalThinking,
                                        isThinking = false
                                    )
                                )
                            )
                        }

                        is GenerationEvent.Failed -> {
                            send(ProcessState.Error("Generation failed: ${state.error.message}"))
                        }

                        is GenerationEvent.OutputItemAdded -> {}

                        is GenerationEvent.ReasoningDelta -> {
                            thinkingAccumulated.append(state.text)
                            val thinkingStr = thinkingAccumulated.toString()

                            val thinkEndTag = "</think>"
                            val thinkEndIndex = thinkingStr.indexOf(thinkEndTag)
                            val jsonIndex = thinkingStr.indexOf('[')

                            val (streamAiResponse, streamThinking, streamIsThinking) = when {
                                accumulated.isNotEmpty() -> {
                                    Triple(accumulated.toString(), thinkingStr, false)
                                }
                                thinkEndIndex != -1 -> {
                                    val after = thinkingStr.substring(thinkEndIndex + thinkEndTag.length).trimStart()
                                    val thoughts = thinkingStr.substring(0, thinkEndIndex).replace("<think>", "").trim()
                                    Triple(after, thoughts, false)
                                }
                                jsonIndex != -1 -> {
                                    val after = thinkingStr.substring(jsonIndex)
                                    val thoughts = thinkingStr.substring(0, jsonIndex).replace("<think>", "").trim()
                                    Triple(after, thoughts, false)
                                }
                                else -> {
                                    Triple("", thinkingStr, true)
                                }
                            }

                            send(
                                ProcessState.Streaming(
                                    AIResponse(
                                        modelName = def.id,
                                        aiResponse = streamAiResponse,
                                        thinkingResponse = streamThinking.ifEmpty { null },
                                        isThinking = streamIsThinking
                                    )
                                )
                            )
                        }

                        is GenerationEvent.Started -> {}

                        is GenerationEvent.TextDelta -> {
                            accumulated.append(state.text)
                            send(
                                ProcessState.Streaming(
                                    AIResponse(
                                        modelName = def.id,
                                        aiResponse = accumulated.toString(),
                                        thinkingResponse = thinkingAccumulated.toString().ifEmpty { null },
                                        isThinking = false
                                    )
                                )
                            )
                        }

                        is GenerationEvent.ToolArgumentsDelta -> {}
                        is GenerationEvent.ToolArgumentsDone -> {}
                        is GenerationEvent.ToolCallAdded -> {}
                        is GenerationEvent.Usage -> {}
                    }
                }
            } catch (e: Exception) {
                println(e)
                send(ProcessState.Error(e.message.toString()))
            }
        }
    }

    private fun extractContent(text: String, thinking: String?): Pair<String, String?> {
        val rawText = text.trim()
        val rawThinking = thinking?.trim().orEmpty()

        // 1. Search for JSON array in rawText first
        val textArrayStart = rawText.indexOf('[')
        val textArrayEnd = rawText.lastIndexOf(']')
        if (textArrayStart != -1 && textArrayEnd > textArrayStart) {
            val jsonCandidate = rawText.substring(textArrayStart, textArrayEnd + 1).trim()
            if (jsonCandidate.contains("\"version\"") || jsonCandidate.contains("\"createSurface\"") || jsonCandidate.contains("\"updateComponents\"")) {
                return Pair(jsonCandidate, rawThinking.ifEmpty { null })
            }
        }

        // 2. Search for JSON object in rawText
        val textObjStart = rawText.indexOf('{')
        val textObjEnd = rawText.lastIndexOf('}')
        if (textObjStart != -1 && textObjEnd > textObjStart) {
            val jsonCandidate = rawText.substring(textObjStart, textObjEnd + 1).trim()
            if (jsonCandidate.contains("\"version\"") || jsonCandidate.contains("\"createSurface\"") || jsonCandidate.contains("\"updateComponents\"")) {
                return Pair(jsonCandidate, rawThinking.ifEmpty { null })
            }
        }

        // 3. Search for JSON array in thinking
        val thinkArrayStart = rawThinking.indexOf('[')
        val thinkArrayEnd = rawThinking.lastIndexOf(']')
        if (thinkArrayStart != -1 && thinkArrayEnd > thinkArrayStart) {
            val jsonCandidate = rawThinking.substring(thinkArrayStart, thinkArrayEnd + 1).trim()
            if (jsonCandidate.contains("\"version\"") || jsonCandidate.contains("\"createSurface\"") || jsonCandidate.contains("\"updateComponents\"")) {
                val thoughts = rawThinking.substring(0, thinkArrayStart).replace("<think>", "").trim()
                return Pair(jsonCandidate, thoughts.ifEmpty { null })
            }
        }

        // 4. Search for JSON object in thinking
        val thinkObjStart = rawThinking.indexOf('{')
        val thinkObjEnd = rawThinking.lastIndexOf('}')
        if (thinkObjStart != -1 && thinkObjEnd > thinkObjStart) {
            val jsonCandidate = rawThinking.substring(thinkObjStart, thinkObjEnd + 1).trim()
            if (jsonCandidate.contains("\"version\"") || jsonCandidate.contains("\"createSurface\"") || jsonCandidate.contains("\"updateComponents\"")) {
                val thoughts = rawThinking.substring(0, thinkObjStart).replace("<think>", "").trim()
                return Pair(jsonCandidate, thoughts.ifEmpty { null })
            }
        }

        if (rawText.isNotEmpty()) {
            return Pair(rawText, rawThinking.ifEmpty { null })
        }

        return Pair(rawThinking.replace("<think>", "").replace("</think>", "").trim(), null)
    }

    override suspend fun deleteModel(modelName: String): Flow<ProcessState<Boolean>> {
        val def = resolveModelDef(modelName)
        return flow {
            try {
                initModel(def.id)
                val model = RunAnywhere.models.get(def.id)
                if (model == null) {
                    emit(ProcessState.Error("Model not found"))
                } else {
                    runCatching { RunAnywhere.models.unload(def.id) }
                    RunAnywhere.models.delete(def.id)
                    emit(ProcessState.Success(true))
                }
            } catch (e: Exception) {
                emit(ProcessState.Error(e.message.toString()))
            }
        }
    }
}
