package com.charan.yourday.data.repository

import com.charan.yourday.data.model.TodoData
import kotlinx.coroutines.flow.Flow

interface TodoistRepository {
    val tasks: Flow<List<TodoData>>
    val isConnected: Flow<Boolean>

    suspend fun requestAuthorization()
    suspend fun exchangeToken(code: String): Result<String>
    suspend fun refreshTasks(): Result<List<TodoData>>
    suspend fun disconnect()
}
