package com.panda.lockscreen.notification

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar
import java.util.Date

class AlarmReminderBroadcastCast : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("AlarmManagerImpl", "onReceive: ")
        val keyguardManager = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val powerManager =
            context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        val isDeviceLockedOrNotInteractive =
            !powerManager.isInteractive || keyguardManager.isKeyguardLocked
//        if (!isDeviceLockedOrNotInteractive) {
//            Log.d("FullScreenReminderReceiver", "Device is interactive and not locked, skipping full-screen intent")
//            return
//        }
        Log.d(
            "AlarmManagerImpl",
            "Device locked or not interactive: $isDeviceLockedOrNotInteractive"
        )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra("schedule_data", Schedule::class.java)
            } else {
                intent?.getParcelableExtra("schedule_data")
            }?.let { schedule ->
                Log.e("AlarmManagerImpl", "onReceive: ${schedule.id}")
                    Log.e(
                        "AlarmManagerImpl",
                        "onReceive: ${schedule.id} repeatTimes ${schedule.repeatTimes}   timesShowed = ${schedule.repeatTimes}",
                    )
                    if (schedule.repeatTimes==1) {
                        Log.e(
                            "AlarmManagerImpl",
                            "onReceive: $schedule ${getCurrentDay()}"
                        )
                        context.let { context ->
                            NotificationManagerImpl(
                                context,
                                schedule
                            ).createNotification(schedule.event)
                        }
                    }
            }

    }

    fun getWeekOfYearUsingCalendar(timeInMillis: Long): Int {
        // Create a Calendar instance
        val calendar = Calendar.getInstance()

        // Set the calendar time to the provided timestamp
        calendar.time = Date(timeInMillis)

        // Get the week of year
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    private fun getCurrentDay(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }

    private fun getCurrentWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    }
}