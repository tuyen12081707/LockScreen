package com.panda.reminderlockscreen.utils

import android.content.Context
import android.content.Intent
import com.panda.reminderlockscreen.notification.Schedule

interface LockScreenIntentProvider {
    fun getMainIntent(context: Context, event: String): Intent
    fun getFullscreenReminderIntent(context: Context, schedule: Schedule): Intent
}
