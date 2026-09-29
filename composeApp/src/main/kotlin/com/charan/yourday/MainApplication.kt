package com.charan.yourday

import android.app.Application
import com.charan.yourday.di.App
import com.splendo.kaluga.base.ApplicationHolder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.plugin.module.dsl.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ApplicationHolder.applicationContext = this

        startKoin<App> {
            androidContext(this@MainApplication)
            androidLogger()
        }
    }
}
