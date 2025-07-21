package com.panda.reminderlockscreen.utils

object LockScreenConfig {
    lateinit var intentProvider: LockScreenIntentProvider

    fun init(intentProvider: LockScreenIntentProvider) {
        this.intentProvider = intentProvider
    }
}
