package com.charan.yourday

import android.app.Application
import android.util.Log
import com.charan.yourday.di.androidModule
import com.charan.yourday.di.initKoin
import com.runanywhere.sdk.llm.llamacpp.LlamaCPP
import com.runanywhere.sdk.public.RunAnywhere
import com.splendo.kaluga.base.ApplicationHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MainApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        ApplicationHolder.applicationContext = this
        initKoin {
            androidContext(this@MainApplication)
            modules(androidModule)
            androidLogger()

        }
        // Register backend + init SDK once. Must happen before any
        // RunAnywhere.models / llm call, otherwise load fails with
        // "no registered backend serves the requested primitive".
        appScope.launch {
            try {
                LlamaCPP.register()
            } catch (e: Exception) {
                Log.w("YourDay", "LlamaCPP.register failed: ${e.message}")
            }
            try {
                if (!RunAnywhere.isInitialized) {
                    RunAnywhere.initialize(context = this@MainApplication)
                }
            } catch (e: Exception) {
                Log.e("YourDay", "RunAnywhere.initialize failed: ${e.message}")
            }
        }
    }
}
