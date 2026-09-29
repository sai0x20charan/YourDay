package com.charan.yourday.permission

enum class PermissionType {
    CALENDAR,
    LOCATION;

    companion object {
        @Deprecated("Use CALENDAR instead", ReplaceWith("CALENDAR"))
        val CALENDER = CALENDAR
    }
}
