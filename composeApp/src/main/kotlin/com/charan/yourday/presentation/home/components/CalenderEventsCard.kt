package com.charan.yourday.presentation.home.components

import androidx.compose.runtime.Composable
import com.charan.yourday.presentation.home.CalenderState

@Composable
fun CalendarCard(
    calenderState : CalenderState,
    grantPermission : () -> Unit) {

    ContentElevatedCard(
        title = "Today's Events",
        isLoading = calenderState.isLoading,
        hasError = calenderState.error,
        hasContent = calenderState.calenderData != null,
        content = {
            if (!calenderState.isCalenderPermissionGranted) {
                GrantPermissionContent("Please Grant Permission to access calender") {
                    grantPermission()

                }
                return@ContentElevatedCard
            }
            if (calenderState.calenderData.isNullOrEmpty().not()) {
                for (event in calenderState.calenderData!!) {
                    EventItem(event)
                }
                return@ContentElevatedCard

            }
            if (calenderState.calenderData.isNullOrEmpty()) {
                NoEventItem()
                return@ContentElevatedCard
            }

        }
    )


}
