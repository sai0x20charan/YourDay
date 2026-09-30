package com.charan.yourday.data.model

enum class LlmDownloadStage {
    DOWNLOADING,
    VERIFYING,
    EXTRACTING,
    COMPLETED
}

data class LlmDownloadStatus(
    val stage: LlmDownloadStage,
    val progress: Float? = null,
    val errorMessage: String? = null
) {
    val isDownloaded: Boolean get() = stage == LlmDownloadStage.COMPLETED
    val isFailed: Boolean get() = errorMessage != null
    val isInProgress: Boolean get() = !isDownloaded && !isFailed
}
