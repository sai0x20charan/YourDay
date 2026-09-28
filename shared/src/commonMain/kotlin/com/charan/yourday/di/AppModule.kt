package com.charan.yourday.di

import com.charan.yourday.data.network.Ktor.ApiService
import com.charan.yourday.data.network.Ktor.createHttpClient
import com.charan.yourday.data.repository.DataStoreRepository
import com.charan.yourday.data.repository.TodoistRepo
import com.charan.yourday.data.repository.WeatherRepo
import com.charan.yourday.data.repository.impl.DataStoreRepositoryImpl
import com.charan.yourday.data.repository.impl.TodoistImp
import com.charan.yourday.data.repository.impl.WeatherRepoImp
import com.charan.yourday.utils.UserPreferencesStore
import com.splendo.kaluga.permissions.base.PermissionsBuilder
import com.splendo.kaluga.permissions.calendar.registerCalendarPermissionIfNotRegistered
import com.splendo.kaluga.permissions.location.registerLocationPermission
import com.splendo.kaluga.permissions.location.registerLocationPermissionIfNotRegistered
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

    val appModule = module {
        single { createHttpClient(get()) }
        factory  { ApiService(client = get()) }
        factory  <WeatherRepo> { WeatherRepoImp(apiService = get(),get()) }
        factory <TodoistRepo>{ TodoistImp(get(),get())  }
        single <UserPreferencesStore>{ UserPreferencesStore() }
        single <DataStoreRepository>{ DataStoreRepositoryImpl(get()) }
        single <PermissionsBuilder>{
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
