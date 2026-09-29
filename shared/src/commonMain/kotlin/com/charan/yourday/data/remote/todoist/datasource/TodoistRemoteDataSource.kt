package com.charan.yourday.data.remote.todoist.datasource

import com.charan.yourday.data.network.dto.TodoistTaskDTO
import com.charan.yourday.data.network.dto.TodoistTodayTasksResponseDTO
import com.charan.yourday.data.network.dto.TodoistTokenDTO
import com.charan.yourday.data.remote.todoist.api.TodoistApi
import com.charan.yourday.utils.ErrorCodes
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class TodoistRemoteDataSource(
    private val todoistApi: TodoistApi
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getAccessToken(code: String): TodoistTokenDTO {
        val response = todoistApi.getAccessToken(code)
        return when (response.status) {
            HttpStatusCode.OK -> response.body<TodoistTokenDTO>()
            HttpStatusCode.Unauthorized -> throw Exception(ErrorCodes.UNAUTHORIZED.name)
            else -> throw Exception("API Error: ${response.status}")
        }
    }

    suspend fun getTodayTasks(token: String): TodoistTodayTasksResponseDTO {
        val response = todoistApi.getTodayTasks(token)
        return when (response.status) {
            HttpStatusCode.OK -> {
                val responseBody = response.bodyAsText()
                if (responseBody.trimStart().startsWith("[")) {
                    val list = json.decodeFromString<List<TodoistTaskDTO>>(responseBody)
                    TodoistTodayTasksResponseDTO(results = list)
                } else {
                    json.decodeFromString<TodoistTodayTasksResponseDTO>(responseBody)
                }
            }
            HttpStatusCode.Unauthorized -> throw Exception(ErrorCodes.UNAUTHORIZED.name)
            else -> throw Exception("API Error: ${response.status}")
        }
    }
}
