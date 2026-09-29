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
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
actual class PlatformModule {
    @Single
    fun permissionsController(context: Context): PermissionsController =
        PermissionsController(applicationContext = context)

    @Factory
    fun locationService(context: Context): LocationServiceRepo =
        LocationServiceImp(context = context)

    @Single
    fun httpClientEngine(): HttpClientEngine =
        OkHttp.create()

    @Factory
    fun permissionManager(context: Context): PermissionManager =
        PermissionManagerImp(context = context)

    @Single
    fun calenderEventsRepo(context: Context): CalenderEventsRepo =
        CalenderEventsImp(context = context)

    @Single
    fun platformSettings(context: Context): PlatformSettings =
        PlatformSettings(context = context)

    @Single
    fun dataStore(context: Context): DataStore<Preferences> =
        createDataStore(context)
}
