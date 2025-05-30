package com.panda.reminderlockscreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.panda.reminderlockscreen.presentation.activity.MainActivity

class NotificationClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG==","NotificationClickReceiver")
        val intentMain = Intent(context, MainActivity::class.java).apply {
            putExtra("isFromLockScreen", true)
        }
        context?.startActivity(intentMain)
    }
}