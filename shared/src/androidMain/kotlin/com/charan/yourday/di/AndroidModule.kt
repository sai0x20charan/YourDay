package com.charan.yourday.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.charan.yourday.data.datastore.createDataStore
import com.charan.yourday.data.local.llm.datasource.LocalLlmDataSource
import com.charan.yourday.data.local.llm.datasource.RunAnywhereLlmDataSource
import com.charan.yourday.data.repository.CalendarEventsRepository
import com.charan.yourday.data.repository.LocationServiceRepository
import com.charan.yourday.data.repository.impl.CalendarEventsRepositoryImpl
import com.charan.yourday.data.repository.impl.LocationServiceRepositoryImpl
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.PermissionManagerImpl
import com.charan.yourday.utils.PlatformSettings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@Configuration("app")
actual class PlatformModule {
    @Factory
    fun provideLocationService(context: Context): LocationServiceRepository =
        LocationServiceRepositoryImpl(context = context)

    @Single
    fun provideHttpClientEngine(): HttpClientEngine =
        OkHttp.create()

    @Factory
    fun providePermissionManager(context: Context): PermissionManager =
        PermissionManagerImpl(context = context)

    @Single
    fun provideCalendarEventsRepo(context: Context): CalendarEventsRepository =
        CalendarEventsRepositoryImpl(context = context)

    @Single
    fun providePlatformSettings(context: Context): PlatformSettings =
        PlatformSettings(context = context)

    @Single
    fun provideDataStore(context: Context): DataStore<Preferences> =
        createDataStore(context)

    @Single
    fun provideLocalLlmDataSource(context: Context): LocalLlmDataSource =
        RunAnywhereLlmDataSource(context = context)
}
