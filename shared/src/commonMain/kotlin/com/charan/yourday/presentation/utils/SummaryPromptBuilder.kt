package com.charan.yourday.presentation.utils

import com.charan.yourday.presentation.home.HomeState
import com.charan.yourday.utils.DateUtils
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object SummaryPromptBuilder {
    @OptIn(ExperimentalTime::class)
    fun HomeState.generateSummaryPrompt(): String {
        val currentState = this

        // Get current time once (device time when user opened the app)
        val timeFormatter = DateUtils.getTimeFromTimeMillis(Clock.System.now().toEpochMilliseconds())

        return buildString {
            appendLine("You are a personal daily briefing assistant — nothing more, nothing less. You act ONLY as a neutral planner agent that gives the user a quick, factual overview of their day.")
            appendLine()
            appendLine("Write in plain prose as if you are a helpful, warm friend giving them a quick daily rundown. Use second-person language (\"You have...\", \"Your day looks...\", \"Today you...\").")
            appendLine("Do NOT use any first-person commitments like \"I'll tackle\", \"I'll handle\", \"I recommend\", \"Let me...\", or anything that sounds like you (the AI) are going to do the tasks. You are only describing, never volunteering or acting.")
            appendLine("Do not use bullet points, headers, emojis, symbols, or markdown of any kind. Do not ask questions. Just deliver the summary and stop.")
            appendLine()
            appendLine("Keep the tone warm and practical. Take the current time into account so the summary feels relevant (e.g. if it's 2 PM, talk about what has already happened today + what's still ahead, not as if the day is just starting).")
            appendLine("Mention the weather and how it might affect the rest of the day, highlight calendar events, and note todos/overdue tasks. If the day looks busy or light, reflect that naturally. Never invent information.")
            appendLine("Limit the summary to 5–7 sentences.")
            appendLine()
            appendLine("INPUT:")
            appendLine()
            appendLine("Current time: $timeFormatter")
            appendLine()
            // Weather
            appendLine("Weather:")
            currentState.weatherState.currentWeather?.let { weather ->
                appendLine("${weather.location}. Temperature ${weather.temp} ${currentState.weatherState.weatherUnits} degrees. ${weather.condition}.")
            } ?: appendLine("No weather data available.")
            appendLine()
            // Todos
            appendLine("Todo Items:")
            val todos = currentState.todoState.todoData ?: emptyList()
            if (todos.isEmpty()) {
                appendLine("No tasks planned.")
            } else {
                todos.filterNot { it.isOverDue }.forEach {
                    appendLine(it.taskName)
                }
                val overdueCount = todos.count { it.isOverDue }
                if (overdueCount > 0) {
                    appendLine("$overdueCount overdue task${if (overdueCount > 1) "s" else "" } from previous days.")
                }
            }
            appendLine()
            // Calendar
            appendLine("Calendar Events:")
            val events = currentState.calendarData.calendarData ?: emptyList()
            if (events.isEmpty()) {
                appendLine("No events scheduled today.")
            } else {
                events.forEach {
                    appendLine(it.title)
                }
            }
            appendLine()
            appendLine("OUTPUT:")
        }
    }
}
