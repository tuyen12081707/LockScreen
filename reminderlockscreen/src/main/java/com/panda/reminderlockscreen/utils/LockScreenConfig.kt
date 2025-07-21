package com.panda.reminderlockscreen.utils

object LockScreenConfig {
      var intentProvider: LockScreenIntentProvider?=null

    fun init(intentProvider: LockScreenIntentProvider) {
        this.intentProvider = intentProvider
    }
}
