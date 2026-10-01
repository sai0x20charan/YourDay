package com.charan.yourday.data.local.llm.datasource

import FoundationModelBridge.FoundationModelBridge
import com.charan.yourday.data.model.AIModelInfo
import com.charan.yourday.data.model.AIResponse
import com.charan.yourday.data.model.LlmDownloadStage
import com.charan.yourday.data.model.LlmDownloadStatus
import com.charan.yourday.data.model.LlmGenerationEvent
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import platform.UIKit.UIDevice
@OptIn(ExperimentalForeignApi::class)
class IosLocalLlmDataSource : LocalLlmDataSource {

    companion object {
        private const val UNSUPPORTED_MESSAGE = "Local LLM is not supported on iOS yet"
    }


    private val foundationModel = FoundationModelBridge()




    override suspend fun init() {

    }

    override suspend fun registerModel(model: AIModelInfo) = Unit

    override fun downloadModel(model: AIModelInfo): Flow<LlmDownloadStatus> = flowOf(
        LlmDownloadStatus(
            stage = LlmDownloadStage.DOWNLOADING,
            errorMessage = UNSUPPORTED_MESSAGE
        )
    )

    override suspend fun isModelDownloaded(model: AIModelInfo): Boolean {


        return true
    }

    override fun generateSummary(
        model: AIModelInfo,
        input: String
    ): Flow<LlmGenerationEvent> = callbackFlow {

        foundationModel.generateResponse(
            input,
            completion = { string, error ->
                trySend(
                    LlmGenerationEvent.Streaming(
                        AIResponse(
                            aiResponse = string ?: ""
                        )
                    )
                )
            }
        )

        awaitClose {
            this.close()
        }
    }

    override suspend fun deleteModel(model: AIModelInfo) =
        throw UnsupportedOperationException(UNSUPPORTED_MESSAGE)
}
