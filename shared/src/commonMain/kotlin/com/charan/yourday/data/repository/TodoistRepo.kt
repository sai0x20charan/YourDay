package com.charan.yourday.data.repository

import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.network.responseDTO.TodoistTokenDTO
import com.charan.yourday.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface TodoistRepo {

    suspend fun requestAuthorization()

    suspend fun getAccessToken(code : String) : Flow<ProcessState<TodoistTokenDTO>>

    suspend fun getTodayTasks(code : String) : Flow<ProcessState<Boolean>>


}