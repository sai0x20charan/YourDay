package com.charan.yourday.data.model

import com.charan.yourday.utils.DateUtils

data class CalendarItems(
    var title: String? = null,
    var description: String? = null,
    var stateTime: Long? = null,
    var endTime: Long? = null,
    var eventId: String? = null,
    var calendarColor: String? = null
) {
    @Deprecated("Use calendarColor instead", ReplaceWith("calendarColor"))
    val calenderColor: String? get() = calendarColor

    fun getEventName(): String {
        return this.title ?: "Unknown Event"
    }

    fun getFormatedStartTime(): String {
        return DateUtils.getTimeFromTimeMillis(this.stateTime ?: 0)
    }

    fun getFormatedEndTime(): String {
        return DateUtils.getTimeFromTimeMillis(this.endTime ?: 0)
    }
}

typealias CalenderItems = CalendarItems
