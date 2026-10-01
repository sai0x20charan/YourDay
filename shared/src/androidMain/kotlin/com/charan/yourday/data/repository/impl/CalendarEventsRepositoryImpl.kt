package com.charan.yourday.data.repository.impl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.charan.yourday.data.model.CalendarItems
import com.charan.yourday.data.repository.CalendarEventsRepository
import com.charan.yourday.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CalendarEventsRepositoryImpl(val context: Context) : CalendarEventsRepository {
    @SuppressLint("Range")
    override suspend fun getCalendarEvents(): List<CalendarItems> = withContext(Dispatchers.IO) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return@withContext emptyList()
        }

        try {
            val currentTime = DateUtils.getCurrentTimeInMillis()
            val stateDayTime = DateUtils.getStartOfDay()
            val endDayTime = DateUtils.getEndOfDay()
            val calendarItems = mutableListOf<CalendarItems>()
            val uri: Uri = CalendarContract.Events.CONTENT_URI
            val projection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DISPLAY_COLOR
            )
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                while (it.moveToNext()) {
                    val startTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTSTART))
                    val endTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTEND))
                    if ((startTime in stateDayTime..endDayTime) ||
                        (endTime in stateDayTime..endDayTime) ||
                        (currentTime in startTime..endTime)) {
                        val calendarItem = CalendarItems(
                            eventId = it.getString(it.getColumnIndex(CalendarContract.Events._ID)),
                            title = it.getString(it.getColumnIndex(CalendarContract.Events.TITLE)),
                            description = it.getString(it.getColumnIndex(CalendarContract.Events.DESCRIPTION)),
                            stateTime = stateDayTime,
                            endTime = endTime,
                            calendarColor = it.getString(it.getColumnIndex(CalendarContract.Events.DISPLAY_COLOR)),
                        )
                        calendarItems.add(calendarItem)
                    }
                }
            }
            calendarItems
        } catch (e: Exception) {
            emptyList()
        }
    }
}

typealias CalenderEventsRepositoryImpl = CalendarEventsRepositoryImpl
