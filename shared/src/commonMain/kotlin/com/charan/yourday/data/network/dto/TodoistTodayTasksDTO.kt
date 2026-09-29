package com.charan.yourday.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoistTodayTasksResponseDTO(
    val results: List<TodoistTaskDTO> = emptyList(),
    @SerialName("next_cursor")
    val nextCursor: String? = null,
)

@Serializable
data class TodoistTaskDTO(
    val id: String = "",
    val content: String = "",
    val description: String = "",
    @SerialName("is_completed")
    val isCompleted: Boolean = false,
    val due: Due? = null,
    val priority: Long? = null,
    @SerialName("project_id")
    val projectId: String? = null,
    @SerialName("section_id")
    val sectionId: String? = null,
    @SerialName("parent_id")
    val parentId: String? = null,
    val url: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val labels: List<String> = emptyList(),
)

typealias Result = TodoistTaskDTO

@Serializable
data class Due(
    val date: String = "",
    val string: String? = null,
    val lang: String? = null,
    val datetime: String? = null,
    val timezone: String? = null,
    @SerialName("is_recurring")
    val isRecurring: Boolean = false,
)
