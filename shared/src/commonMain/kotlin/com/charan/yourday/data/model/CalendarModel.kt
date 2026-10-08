package com.charan.yourday.data.model

import com.charan.yourday.utils.DateUtils

data class CalendarItems(
    var title: String? = null,
    var description: String? = null,
    var stateTime: Long? = null,
    var endTime: Long? = null,
    var eventId: String? = null,
    var calendarColor: String? = null
)

typealias CalenderItems = CalendarItems
