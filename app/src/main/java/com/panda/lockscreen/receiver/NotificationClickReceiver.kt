package com.panda.lockscreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.panda.lockscreen.presentation.activity.TestActivity

class NotificationClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG==","NotificationClickReceiver")
        val intentMain = Intent(context, TestActivity::class.java).apply {
            putExtra("isFromLockScreen", true)
        }
        context?.startActivity(intentMain)
    }
}