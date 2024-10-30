package com.vaalzebub.fintatechtest

import android.app.Application
import com.vaalzebub.fintatechtest.di.loadModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(loadModule())
        }
    }
}