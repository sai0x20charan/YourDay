package com.charan.yourday

import android.app.Application
import com.charan.yourday.di.androidModule
import com.charan.yourday.di.initKoin
import com.splendo.kaluga.base.ApplicationHolder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ApplicationHolder.applicationContext = this

        initKoin {
            androidContext(this@MainApplication)
            modules(androidModule)
            androidLogger()

        }
    }
}