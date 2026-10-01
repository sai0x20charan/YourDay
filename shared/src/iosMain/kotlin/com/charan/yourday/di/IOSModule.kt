package com.charan.yourday.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.charan.yourday.data.datastore.createDataStore
import com.charan.yourday.data.repository.CalendarEventsRepository
import com.charan.yourday.data.repository.LocationServiceRepository
import com.charan.yourday.data.repository.impl.CalendarEventsRepositoryImpl
import com.charan.yourday.data.repository.impl.LocationServiceRepositoryImpl
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.PermissionManagerImpl
import com.charan.yourday.utils.PlatformSettings
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
    fun provideLocationService(): LocationServiceRepository = LocationServiceRepositoryImpl()

    @Single
    fun provideHttpClientEngine(): HttpClientEngine = Darwin.create()

    @Single
    fun providePermissionManager(): PermissionManager = PermissionManagerImpl()

    @Single
    fun provideCalendarEventsRepo(): CalendarEventsRepository = CalendarEventsRepositoryImpl()

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
