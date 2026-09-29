package com.charan.yourday.data.network.dto

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class TodoistTodayTasksResponseDTO(
    val results: List<Result>,
    @SerialName("next_cursor")
    val nextCursor: String?,
)

@Serializable
data class Result(
    @SerialName("user_id")
    val userId: String,
    val id: String,
    @SerialName("project_id")
    val projectId: String,
    @SerialName("section_id")
    val sectionId: String?,
    @SerialName("parent_id")
    val parentId: String?,
    @SerialName("added_by_uid")
    val addedByUid: String,
    @SerialName("assigned_by_uid")
    val assignedByUid: String?,
    @SerialName("responsible_uid")
    val responsibleUid: String?,
    val labels: List<String>,
    val deadline: String?,
    val duration: String?,
    val checked: Boolean,
    @SerialName("is_deleted")
    val isDeleted: Boolean,
    @SerialName("added_at")
    val addedAt: String,
    @SerialName("completed_at")
    val completedAt: String?,
    @SerialName("completed_by_uid")
    val completedByUid: String?,
    @SerialName("updated_at")
    val updatedAt: String,
    val due: Due?,
    val priority: Long,
    @SerialName("child_order")
    val childOrder: Long,
    val content: String,
    val description: String,
    @SerialName("note_count")
    val noteCount: Long,
    @SerialName("day_order")
    val dayOrder: Long,
    @SerialName("is_collapsed")
    val isCollapsed: Boolean,
)

@Serializable
data class Due(
    val date: String,
    val timezone: String?,
    val string: String,
    val lang: String,
    @SerialName("is_recurring")
    val isRecurring: Boolean,
)
