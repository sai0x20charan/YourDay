package com.charan.yourday.di

import android.content.Context
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
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    factory<LocationServiceRepository> { LocationServiceRepositoryImpl(context = androidContext()) }
    single { OkHttp.create() }
    factory<PermissionManager> {
        PermissionManagerImpl(
            context = androidContext(),
        )
    }
    single<CalendarEventsRepository> { CalendarEventsRepositoryImpl(context = androidContext()) }
    single<PlatformSettings> { PlatformSettings(context = androidContext()) }
    single<DataStore<Preferences>> { createDataStore(context = get<Context>()) }
}
