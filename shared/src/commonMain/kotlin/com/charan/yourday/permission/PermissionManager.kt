package com.charan.yourday.permission

import kotlinx.coroutines.flow.Flow

interface PermissionManager {

    fun isPermissionGranted(permissions: PermissionType) : Boolean
    fun requestPermission(permissions: PermissionType)
    fun requestMultiplePermissions(permissions: List<PermissionType>)
    fun openAppSettings()
    fun observeCalenderPermission() : Flow<Boolean>


}