package com.charan.yourday.di

import com.charan.yourday.data.network.ktor.createHttpClient
import com.charan.yourday.data.remote.todoist.api.TodoistApi
import com.charan.yourday.data.remote.todoist.datasource.TodoistRemoteDataSource
import com.charan.yourday.data.remote.weather.api.WeatherApi
import com.charan.yourday.data.remote.weather.datasource.WeatherRemoteDataSource
import com.charan.yourday.data.repository.TodoistRepository
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.data.repository.WeatherRepository
import com.charan.yourday.data.repository.impl.TodoistRepositoryImpl
import com.charan.yourday.data.repository.impl.UserPreferencesRepositoryImpl
import com.charan.yourday.data.repository.impl.WeatherRepositoryImpl
import com.splendo.kaluga.permissions.base.PermissionsBuilder
import com.splendo.kaluga.permissions.calendar.registerCalendarPermissionIfNotRegistered
import com.splendo.kaluga.permissions.location.registerLocationPermissionIfNotRegistered
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    single { createHttpClient(get()) }

    // Weather Remote Data Source
    single { WeatherApi(client = get()) }
    single { WeatherRemoteDataSource(weatherApi = get()) }

    // Todoist Remote Data Source
    single { TodoistApi(client = get()) }
    single { TodoistRemoteDataSource(todoistApi = get()) }

    // Repositories
    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(
            dataStore = get()
        )
    }
    single<WeatherRepository> {
        WeatherRepositoryImpl(
            weatherRemoteDataSource = get(),
            dataStore = get()
        )
    }
    single<TodoistRepository> {
        TodoistRepositoryImpl(
            todoistRemoteDataSource = get(),
            userPreferencesRepository = get(),
            dataStore = get()
        )
    }

    single<PermissionsBuilder> {
        PermissionsBuilder()
            .apply {
                this.registerLocationPermissionIfNotRegistered()
                this.registerCalendarPermissionIfNotRegistered()
            }
    }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(appModule)
}
