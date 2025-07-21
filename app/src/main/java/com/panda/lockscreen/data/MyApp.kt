package com.panda.lockscreen.data

import android.app.Application
import com.google.firebase.FirebaseApp
import com.panda.lockscreen.data.prefernces.AppConfigManager
import com.panda.reminderlockscreen.utils.LockScreenConfig

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppConfigManager.initialize(this)
    }
}
