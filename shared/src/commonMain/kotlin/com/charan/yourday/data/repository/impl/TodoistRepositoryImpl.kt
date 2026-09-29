package com.charan.yourday.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.charan.yourday.BuildKonfig
import com.charan.yourday.data.mapper.toTodoData
import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.remote.todoist.datasource.TodoistRemoteDataSource
import com.charan.yourday.data.repository.TodoistRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.utils.OpenURL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TodoistRepositoryImpl(
    private val todoistRemoteDataSource: TodoistRemoteDataSource,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val dataStore: DataStore<Preferences>
) : TodoistRepository {

    companion object {
        private const val TODOIST_BASE_URL = "todoist.com"
        private val TODO_DATA_KEY = stringPreferencesKey("todo_data")
        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    override val tasks: Flow<List<TodoData>> = dataStore.data.map { preferences ->
        preferences[TODO_DATA_KEY]?.let { jsonString ->
            runCatching {
                json.decodeFromString<List<TodoData>>(jsonString)
            }.getOrNull()
        } ?: emptyList()
    }

    override val isConnected: Flow<Boolean> =
        userPreferencesRepository.todoistAccessToken.map { !it.isNullOrBlank() }

    override suspend fun requestAuthorization() {
        OpenURL.openURL(
            "https://" + TODOIST_BASE_URL + "/oauth/authorize?client_id=${BuildKonfig.TODOIST_CLIENT_ID}&scope=data:read&state=secretstring"
        )
    }

    override suspend fun exchangeToken(code: String): Result<String> {
        return runCatching {
            val tokenDTO = todoistRemoteDataSource.getAccessToken(code)
            val token = tokenDTO.access_token ?: error("Access token is null")
            userPreferencesRepository.setTodoistAccessToken(token)
            token
        }
    }

    override suspend fun refreshTasks(): Result<List<TodoData>> {
        return runCatching {
            val token = userPreferencesRepository.todoistAccessToken.first()
            if (token.isNullOrBlank()) {
                dataStore.edit { preferences ->
                    preferences.remove(TODO_DATA_KEY)
                }
                return@runCatching emptyList()
            }
            val tasksDTO = todoistRemoteDataSource.getTodayTasks(token)
            val mapped = tasksDTO.toTodoData()
            dataStore.edit { preferences ->
                preferences[TODO_DATA_KEY] = json.encodeToString(mapped)
            }
            mapped
        }
    }

    override suspend fun disconnect() {
        userPreferencesRepository.clearTodoistAccessToken()
        dataStore.edit { preferences ->
            preferences.remove(TODO_DATA_KEY)
        }
    }
}
