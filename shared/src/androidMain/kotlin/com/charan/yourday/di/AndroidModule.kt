package com.charan.yourday.di


import android.app.Activity
import com.charan.yourday.createDataStore
import com.charan.yourday.data.repository.A2uiRepository
import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.data.repository.LocalLLMRepository
import com.charan.yourday.data.repository.LocationServiceRepo
import com.charan.yourday.data.repository.impl.A2uiRepositoryImpl
import com.charan.yourday.data.repository.impl.CalenderEventsImp
import com.charan.yourday.data.repository.impl.LocalLLMRepositoryImpl
import com.charan.yourday.data.repository.impl.LocationServiceImp
import com.charan.yourday.permission.PermissionManager
import com.charan.yourday.permission.PermissionManagerImp
import com.charan.yourday.utils.PlatformSettings
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { PermissionsController(applicationContext = androidContext()) }
    factory   <LocationServiceRepo>{ LocationServiceImp(context = androidContext())  }
    single { OkHttp.create() }
    factory<PermissionManager> {
        PermissionManagerImp(
            context = androidContext(),
        )
    }
    single <CalenderEventsRepo> { CalenderEventsImp(context = androidContext())}
    single <PlatformSettings>{ PlatformSettings(context = androidContext()) }
    single { createDataStore(get()) }
    single <LocalLLMRepository>{ LocalLLMRepositoryImpl(get()) }
    single <A2uiRepository>{ A2uiRepositoryImpl() }

}