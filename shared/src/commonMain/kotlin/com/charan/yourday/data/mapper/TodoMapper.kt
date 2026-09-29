package com.charan.yourday.data.mapper

import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.network.dto.TodoistTaskDTO
import com.charan.yourday.data.network.dto.TodoistTodayTasksResponseDTO
import com.charan.yourday.utils.DateUtils.isOverDue
import com.charan.yourday.utils.DateUtils.toLocalDateTime
import com.charan.yourday.utils.TodoProvidersEnums

fun TodoistTodayTasksResponseDTO.toTodoData(): List<TodoData> {
    return this.results.toTodoData()
}

fun List<TodoistTaskDTO>.toTodoData(): List<TodoData> {
    val todoList = mutableListOf<TodoData>()
    this.forEach {
        val dueDate = try {
            (it.due?.datetime ?: it.due?.date)?.takeIf { d -> d.isNotBlank() }?.toLocalDateTime()
        } catch (_: Exception) {
            null
        }
        todoList.add(
            TodoData(
                id = it.id,
                tasks = it.content,
                todoProvider = TodoProvidersEnums.TODOIST.name,
                date = dueDate,
                taskLink = it.url ?: "todoist://task?id=${it.id}",
                isOverDue = dueDate?.isOverDue()
            )
        )
    }
    return todoList
}
