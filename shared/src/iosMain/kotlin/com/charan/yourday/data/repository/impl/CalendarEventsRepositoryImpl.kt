package com.charan.yourday.data.repository.impl

import com.charan.yourday.data.model.CalendarItems
import com.charan.yourday.data.repository.CalendarEventsRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.EventKit.EKEntityType
import platform.EventKit.EKEvent
import platform.EventKit.EKEventStore
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarOptions
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSDate
import platform.Foundation.dateByAddingTimeInterval
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIColor

class CalendarEventsRepositoryImpl : CalendarEventsRepository {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getCalendarEvents(): List<CalendarItems> = withContext(Dispatchers.Default) {
        val eventStore = EKEventStore()
        val calendars = eventStore.calendarsForEntityType(EKEntityType.EKEntityTypeEvent)
        val now = NSDate()

        val calendar = NSCalendar.currentCalendar
        val startOfDay = calendar.startOfDayForDate(now)
        val oneDayFromNow = calendar.dateByAddingUnit(
            unit = NSCalendarUnitDay,
            value = 1,
            toDate = startOfDay,
            NSCalendarOptions.MIN_VALUE
        )?.dateByAddingTimeInterval(-1.0)
        val range = eventStore.predicateForEventsWithStartDate(
            now,
            oneDayFromNow!!,
            calendars
        )
        eventStore.eventsMatchingPredicate(range).mapNotNull { event ->
            event as? EKEvent
        }.map {
            CalendarItems(
                eventId = it.eventIdentifier,
                title = it.title,
                stateTime = it.startDate?.timeIntervalSince1970?.toLong(),
                endTime = it.endDate?.timeIntervalSince1970?.toLong(),
                calendarColor = UIColor(it.calendar?.CGColor).toString()
            )
        }
    }
}

typealias CalenderEventsRepositoryImpl = CalendarEventsRepositoryImpl
