package com.charan.yourday.presentation.home.components

import androidx.compose.runtime.Composable
import com.charan.yourday.presentation.home.CalendarState

@Composable
fun CalendarCard(
    calendarState: CalendarState,
    grantPermission: () -> Unit
) {
    ContentElevatedCard(
        title = "Today's Events",
        isLoading = calendarState.isLoading,
        hasError = calendarState.error,
        hasContent = calendarState.calendarData != null,
        content = {
            if (!calendarState.isCalendarPermissionGranted) {
                GrantPermissionContent("Please Grant Permission to access calendar") {
                    grantPermission()
                }
                return@ContentElevatedCard
            }
            if (!calendarState.calendarData.isNullOrEmpty()) {
                for (event in calendarState.calendarData) {
                    EventItem(event)
                }
                return@ContentElevatedCard
            }
            if (calendarState.calendarData.isNullOrEmpty()) {
                NoEventItem()
                return@ContentElevatedCard
            }
        }
    )
}
