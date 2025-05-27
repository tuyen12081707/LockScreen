package com.panda.lockscreen

import android.app.Application
import com.google.firebase.FirebaseApp
import com.panda.lockscreen.data.prefernces.AppConfigManager
import com.panda.lockscreen.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppConfigManager.initialize(this)
        startKoin {
            androidContext(this@MyApp)
        }
    }
}
