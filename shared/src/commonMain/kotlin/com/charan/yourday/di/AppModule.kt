package com.charan.yourday.di

import com.charan.yourday.data.network.Ktor.createHttpClient
import com.splendo.kaluga.permissions.base.PermissionsBuilder
import com.splendo.kaluga.permissions.calendar.registerCalendarPermissionIfNotRegistered
import com.splendo.kaluga.permissions.location.registerLocationPermissionIfNotRegistered
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.dsl.KoinAppDeclaration
import org.koin.plugin.module.dsl.startKoin

@Module(includes = [PlatformModule::class])
@ComponentScan("com.charan.yourday")
class AppModule {
    @Single
    fun provideHttpClient(engine: HttpClientEngine): HttpClient =
        createHttpClient(engine)

    @Single
    fun providePermissionsBuilder(): PermissionsBuilder =
        PermissionsBuilder().apply {
            registerLocationPermissionIfNotRegistered()
            registerCalendarPermissionIfNotRegistered()
        }
}

@Module
expect class PlatformModule()

@KoinApplication(modules = [AppModule::class])
class YourDayApp

fun initKoin(appDeclaration: KoinAppDeclaration? = null) = startKoin<YourDayApp>(appDeclaration)
