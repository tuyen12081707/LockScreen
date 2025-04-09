package com.panda.lockscreen

import android.app.Application
import com.panda.lockscreen.di.databaseModule
import com.panda.lockscreen.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(listOf(databaseModule, viewModelModule))
        }
    }
}