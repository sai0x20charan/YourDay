package com.charan.yourday.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.charan.yourday.createDataStore
import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.impl.CalenderEventsImp
import com.charan.yourday.data.repository.impl.LocationServiceImp
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.PermissionManagerImp
import com.charan.yourday.utils.PlatformSettings
import dev.icerock.moko.permissions.ios.PermissionsController
import dev.icerock.moko.permissions.ios.PermissionsControllerProtocol
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
actual class PlatformModule {
    @Single
    fun locationService(): LocationServiceRepo = LocationServiceImp()

    @Single
    fun permissionsController(): PermissionsControllerProtocol = PermissionsController()

    @Single
    fun httpClientEngine(): HttpClientEngine = Darwin.create()

    @Single
    fun permissionManager(): PermissionManager = PermissionManagerImp()

    @Single
    fun calenderEventsRepo(): CalenderEventsRepo = CalenderEventsImp()

    @Single
    fun platformSettings(): PlatformSettings = PlatformSettings()

    @Single
    fun dataStore(): DataStore<Preferences> = createDataStore()
}

class KointInitHelper {
    fun initKoin() {
        com.charan.yourday.di.initKoin()
    }
}
