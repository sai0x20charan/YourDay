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
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.plugin.module.dsl.startKoin

@Module
@Configuration("app")
actual class PlatformModule {
    @Single
    fun provideLocationService(): LocationServiceRepo = LocationServiceImp()

    @Single
    fun providePermissionsController(): PermissionsControllerProtocol = PermissionsController()

    @Single
    fun provideHttpClientEngine(): HttpClientEngine = Darwin.create()

    @Single
    fun providePermissionManager(): PermissionManager = PermissionManagerImp()

    @Single
    fun provideCalenderEventsRepo(): CalenderEventsRepo = CalenderEventsImp()

    @Single
    fun providePlatformSettings(): PlatformSettings = PlatformSettings()

    @Single
    fun provideDataStore(): DataStore<Preferences> = createDataStore()
}

class KointInitHelper {
    fun initKoin() {
        startKoin<App> {}
    }
}
