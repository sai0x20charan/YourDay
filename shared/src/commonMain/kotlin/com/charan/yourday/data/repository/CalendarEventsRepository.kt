package com.charan.yourday.data.repository

import com.charan.yourday.data.model.CalendarItems

interface CalendarEventsRepository {
    suspend fun getCalendarEvents(): List<CalendarItems>
}

typealias CalenderEventsRepository = CalendarEventsRepository
