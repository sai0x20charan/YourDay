package com.charan.yourday.permission

import com.charan.yourday.utils.asCommonFlow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorized
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.EventKit.EKAuthorizationStatusAuthorized
import platform.EventKit.EKAuthorizationStatusFullAccess
import platform.EventKit.EKEntityType
import platform.EventKit.EKEventStore
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

class PermissionManagerImpl : PermissionManager {
    private var store = EKEventStore()
    private var location = CLLocationManager()
    private var calendarPermission = MutableStateFlow(false)

    override fun isPermissionGranted(permissions: PermissionType): Boolean {
        return when (permissions) {
            PermissionType.CALENDAR -> {
                store = EKEventStore()
                val status = EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeEvent)
                val isGranted = status == EKAuthorizationStatusAuthorized || status == EKAuthorizationStatusFullAccess
                isGranted
            }

            PermissionType.LOCATION -> {
                location.locationServicesEnabled()
            }
        }
    }

    override fun requestPermission(permissions: PermissionType) {
        when (permissions) {
            PermissionType.CALENDAR -> {
                store.requestFullAccessToEventsWithCompletion { isgranted, _ ->
                    if (isgranted) {
                        calendarPermission.tryEmit(true)
                    } else {
                        calendarPermission.tryEmit(false)
                    }
                }
            }
            PermissionType.LOCATION -> {
                val status = CLLocationManager.authorizationStatus()
                when (status) {
                    kCLAuthorizationStatusAuthorized -> {}
                    kCLAuthorizationStatusNotDetermined -> {
                        location.requestWhenInUseAuthorization()
                    }
                }
            }
        }
    }

    override fun requestMultiplePermissions(permissions: List<PermissionType>) {
        permissions.forEach {
            requestPermission(it)
        }
    }

    override fun openAppSettings() {
        val settingsUrl: NSURL = NSURL.URLWithString(UIApplicationOpenSettingsURLString)!!
        UIApplication.sharedApplication.openURL(settingsUrl, mapOf<Any?, Any>(), null)
    }

    override fun observeCalendarPermission() = calendarPermission.asCommonFlow()
}
