package com.charan.yourday.data.remote.todoist.api

import com.charan.yourday.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import io.ktor.http.path
import org.koin.core.annotation.Single

@Single
class TodoistApi(
    private val client: HttpClient
) {
    companion object {
        private const val TODOIST_AUTH_BASE_URL = "todoist.com"
        private const val TODOIST_API_BASE_URL = "api.todoist.com"
    }

    suspend fun getAccessToken(code: String): HttpResponse {
        return client.post {
            url {
                host = TODOIST_AUTH_BASE_URL
                path("/oauth/access_token")
            }
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("client_id", BuildKonfig.TODOIST_CLIENT_ID)
                        append("client_secret", BuildKonfig.TODOIST_CLIENT_SECRET)
                        append("code", code)
                        append("redirect_uri", "https://yourday.vercel.app/authentication")
                    }
                )
            )
        }
    }

    suspend fun getTodayTasks(token: String): HttpResponse {
        return client.get {
            url {
                host = TODOIST_API_BASE_URL
                headers {
                    append("Authorization", "Bearer $token")
                    append("Accept", "application/json")
                }
                path("api/v1/tasks")
                parameters.append("filter", "today|overdue")
            }
        }
    }
}
