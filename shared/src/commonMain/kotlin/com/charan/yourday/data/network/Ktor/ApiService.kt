package com.charan.yourday.data.network.Ktor

import com.charan.yourday.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import io.ktor.http.append
import io.ktor.http.path

const val weather_base_url = "api.weatherapi.com"
const val todoist_base_url = "todoist.com"
const val todoist_api_url = "api.todoist.com"

class ApiService(val client: HttpClient) {
    suspend fun getCurrentWeather(lat: Double, long: Double): HttpResponse {
        return client.get {
            url {
                host = weather_base_url
                path("current.json")
                parameters.append("q", "$lat,$long")
                parameters.append("key", BuildKonfig.API_KEY)
            }
        }
    }

    suspend fun getForecastWeather(lat: Double, long: Double): HttpResponse {
        return client.get {
            url {
                host = weather_base_url
                path("/v1/forecast.json")
                parameters.append("q", "$lat,$long")
                parameters.append("key", BuildKonfig.API_KEY)
            }
        }
    }

    suspend fun getTodoistAccessToken(code: String): HttpResponse {
        return client.post {
            url {
                host = todoist_base_url
                path("/oauth/access_token")
            }
            setBody(FormDataContent(Parameters.build {
                append("client_id", BuildKonfig.TODOIST_CLIENT_ID)
                append("client_secret", BuildKonfig.TODOIST_CLIENT_SECRET)
                append("code", code)
                append("redirect_uri", "https://yourday.vercel.app/authentication")
            }))
        }
    }

    suspend fun getTodoistTodayTasks(code: String): HttpResponse {
        return client.get {
            url {
                host = todoist_api_url
                path("api/v1/tasks/filter")
                parameters.append("query", "today|overdue")
            }
            headers {
                append("Authorization", "Bearer $code")
                append("Accept", "application/json")
            }
        }
    }
}
