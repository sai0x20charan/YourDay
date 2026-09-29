package com.charan.yourday.data.mapper

import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.network.dto.TodoistTodayTasksResponseDTO
import com.charan.yourday.utils.DateUtils.isOverDue
import com.charan.yourday.utils.DateUtils.toLocalDateTime
import com.charan.yourday.utils.TodoProvidersEnums

fun TodoistTodayTasksResponseDTO.toTodoData(): List<TodoData> {
    val todoList = mutableListOf<TodoData>()
    this.results.forEach {
        val dueDate = it.due?.date?.toLocalDateTime()
        todoList.add(
            TodoData(
                id = it.id,
                tasks = it.content,
                todoProvider = TodoProvidersEnums.TODOIST.name,
                date = dueDate,
                taskLink = "todoist://task?id=${it.id}",
                isOverDue = dueDate?.isOverDue()
            )
        )
    }
    return todoList
}
