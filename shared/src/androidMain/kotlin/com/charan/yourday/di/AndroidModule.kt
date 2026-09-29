package com.charan.yourday.di

import android.content.Context
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
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@Configuration("app")
actual class PlatformModule {
    @Single
    fun providePermissionsController(context: Context): PermissionsController =
        PermissionsController(applicationContext = context)

    @Factory
    fun provideLocationService(context: Context): LocationServiceRepo =
        LocationServiceImp(context = context)

    @Single
    fun provideHttpClientEngine(): HttpClientEngine =
        OkHttp.create()

    @Factory
    fun providePermissionManager(context: Context): PermissionManager =
        PermissionManagerImp(context = context)

    @Single
    fun provideCalenderEventsRepo(context: Context): CalenderEventsRepo =
        CalenderEventsImp(context = context)

    @Single
    fun providePlatformSettings(context: Context): PlatformSettings =
        PlatformSettings(context = context)

    @Single
    fun provideDataStore(context: Context): DataStore<Preferences> =
        createDataStore(context)
}
