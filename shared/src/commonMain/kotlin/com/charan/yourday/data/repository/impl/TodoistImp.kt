package com.charan.yourday.data.repository.impl

import com.charan.yourday.BuildKonfig
import com.charan.yourday.data.mapper.toTodoData
import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.network.Ktor.ApiService
import com.charan.yourday.data.network.Ktor.todoist_base_url
import com.charan.yourday.data.network.responseDTO.TodoistTodayTasksResponseDTO
import com.charan.yourday.data.network.responseDTO.TodoistTokenDTO
import com.charan.yourday.data.repository.DataStoreRepository
import com.charan.yourday.data.repository.TodoistRepo
import com.charan.yourday.utils.ErrorCodes
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.OpenURL
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory(binds = [TodoistRepo::class])
class TodoistImp(
    private val apiService: ApiService,
    private val dataStoreRepository: DataStoreRepository
) : TodoistRepo {
    override suspend fun requestAuthorization() {
        OpenURL.openURL("https://"+todoist_base_url + "/oauth/authorize?client_id=${BuildKonfig.TODOIST_CLIENT_ID}&scope=data:read&state=secretstring")
    }

    override suspend fun getAccessToken(code: String): Flow<ProcessState<TodoistTokenDTO>> = flow{
        emit(ProcessState.Loading)
        try {
            val response = apiService.getTodoistAccessToken(code)
            when(response.status){
                HttpStatusCode.OK -> {
                    dataStoreRepository.setTodoistAccessToken(response.body<TodoistTokenDTO>().access_token ?: "")
                    emit(ProcessState.Success(response.body<TodoistTokenDTO>()))
                }
                HttpStatusCode.Unauthorized -> {
                    emit(ProcessState.Error(ErrorCodes.UNAUTHORIZED.name))
                }
                else -> {
                    emit(ProcessState.Error("API Error: ${response.status}"))
                }

            }
        } catch (e :Exception){
            emit(ProcessState.Error(e.message ?: "Unknown Error"))
        }
    }

    override suspend fun getTodayTasks(code : String): Flow<ProcessState<Boolean>> =flow{
        println("Todoist Access Token: $code")
        emit(ProcessState.Loading)
        try {
            val response = apiService.getTodoistTodayTasks(code)
            when(response.status){
                HttpStatusCode.OK -> {
                    val todoData = response.body<TodoistTodayTasksResponseDTO>().toTodoData()
                    dataStoreRepository.setTodoData(todoData)
                    emit(ProcessState.Success(true))
                }
                HttpStatusCode.Unauthorized -> {
                    emit(ProcessState.Error(ErrorCodes.UNAUTHORIZED.name))
                }
                else -> {
                    emit(ProcessState.Error("API Error: ${response.status}"))
                }

            }
        } catch (e :Exception){
            println(e.message)
            emit(ProcessState.Error(e.message ?: "Unknown Error"))
        }

    }

}
