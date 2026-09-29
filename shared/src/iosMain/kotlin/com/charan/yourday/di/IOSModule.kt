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
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

val iosModule = module {
    single<LocationServiceRepository> { LocationServiceRepositoryImpl() }
    single { Darwin.create() }
    single<PermissionManager> { PermissionManagerImpl() }
    single<CalendarEventsRepository> { CalendarEventsRepositoryImpl() }
    single<DataStore<Preferences>> { createDataStore() }
}

class KointInitHelper() {
    fun initKoin() {
        initKoin {
            modules(iosModule)
        }
    }
}
