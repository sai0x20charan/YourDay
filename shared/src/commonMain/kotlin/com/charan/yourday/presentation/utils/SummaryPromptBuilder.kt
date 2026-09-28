package com.charan.yourday.presentation.utils

import com.charan.yourday.presentation.home.HomeState
import com.charan.yourday.utils.DateUtils
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object SummaryPromptBuilder {

    @OptIn(ExperimentalTime::class)
    fun HomeState.generateSummaryPrompt(): String {
        val currentState = this

        val currentTime = DateUtils.getTimeFromTimeMillis(
            Clock.System.now().toEpochMilliseconds()
        )

        val weatherInfo = currentState.weatherState.currentWeather?.let { weather ->
            "${weather.location}, ${weather.temp} ${currentState.weatherState.weatherUnits}, ${weather.condition}"
        } ?: "No weather data"

        val todos = currentState.todoState.todoData?.map { todo ->
            todo.taskName + if (todo.isOverDue) " (Overdue)" else ""
        }?.take(5) ?: emptyList()

        val events = currentState.calenderData.calenderData?.map { it.title }?.take(5) ?: emptyList()

        val taskSummary = if (todos.isEmpty()) "No pending tasks" else todos.joinToString("; ")
        val eventSummary = if (events.isEmpty()) "No events scheduled" else events.joinToString("; ")

        return buildString {
            appendLine("Output an A2UI v0.9.1 JSON object for daily briefing.")
            appendLine("Current status: Time is $currentTime. Weather is $weatherInfo. Tasks: $taskSummary. Events: $eventSummary.")
            appendLine()
            appendLine("Example JSON output:")
            appendLine("""{"version":"v0.9.1","updateComponents":{"surfaceId":"daily_briefing","components":[{"id":"root","component":"Column","children":["title","gist","weather","timeline"]},{"id":"title","component":"Text","text":"Good day!","variant":"h2"},{"id":"gist","component":"Text","text":"Here is your daily update.","variant":"body"},{"id":"weather","component":"Text","text":"$weatherInfo","variant":"caption"},{"id":"timeline","component":"Text","text":"$taskSummary","variant":"body"}]}}""")
            appendLine()
            appendLine("Output your daily briefing JSON:")
        }
    }
}
