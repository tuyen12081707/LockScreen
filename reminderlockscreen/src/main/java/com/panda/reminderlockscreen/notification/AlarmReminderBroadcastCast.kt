package com.panda.reminderlockscreen.notification

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar
import java.util.Date

class AlarmReminderBroadcastCast : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        Log.e("AlarmManagerImpl", "onReceive: Triggered")

        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        val isDeviceLockedOrNotInteractive = !powerManager.isInteractive || keyguardManager.isKeyguardLocked
        Log.d("AlarmManagerImpl", "Device locked or not interactive: $isDeviceLockedOrNotInteractive")

        val prefs = context.getSharedPreferences(LockScreenSDK.PREF_NAME, Context.MODE_PRIVATE)
        val activeVersion = prefs.getInt(LockScreenSDK.KEY_ACTIVE_VERSION, 1) // Mặc định là 1 (V1 cũ)

        val schedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("schedule_data", Schedule::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("schedule_data")
        }

        if (schedule == null) {
            Log.e("AlarmManagerImpl", "Schedule data is null! Bỏ qua.")
            return
        }

        if (activeVersion == 2) {
            handleV2(context, prefs, schedule)
        } else {
            handleV1(context, schedule)
        }
    }

    private fun handleV1(context: Context, schedule: Schedule) {
        Log.e("AlarmManagerImpl", "onReceive V1: ID = ${schedule.id}, repeatTimes = ${schedule.repeatTimes}")

        // Hiện thông báo đè màn hình
        NotificationManagerImpl(context, schedule).createNotification(schedule)

        // Logic lặp lại cũ của V1
        if (schedule.repeatTimes == 1) {
            Log.e("AlarmManagerImpl", "onReceive V1: Reschedule ${getCurrentDay()}")
            AlarmManagerImpl(context).schedule(schedule)
        }
    }

    private fun handleV2(context: Context, prefs: android.content.SharedPreferences, schedule: Schedule) {
        Log.d("AlarmManagerImpl", "onReceive V2: Kích hoạt kịch bản thông minh")

        if (!prefs.getBoolean(LockScreenSDK.KEY_ENABLED, true)) {
            Log.d("AlarmManagerImpl", "V2: SDK đang bị tắt cấu hình trên Firebase.")
            return
        }

        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val lastDisplayDay = prefs.getInt(LockScreenSDK.KEY_LAST_DISPLAY_DAY, -1)
        var displayCountToday = prefs.getInt(LockScreenSDK.KEY_DISPLAY_COUNT_TODAY, 0)

        if (currentDay != lastDisplayDay) {
            displayCountToday = 0
            prefs.edit().putInt(LockScreenSDK.KEY_LAST_DISPLAY_DAY, currentDay).apply()
        }

        val maxDisplay = prefs.getInt(LockScreenSDK.KEY_MAX_DISPLAY, 5)
        if (displayCountToday >= maxDisplay) {
            Log.w("AlarmManagerImpl", "V2: Đạt giới hạn hiển thị trong ngày ($displayCountToday/$maxDisplay). Bỏ qua lượt này.")
            // Quá số lần trong ngày thì không lặp interval nữa, mà set báo thức sang mốc giờ của ngày hôm sau
            LockScreenSDK.scheduleDynamicV2Alarm(context, triggerAfterInterval = false)
            return
        }

        NotificationManagerImpl(context, schedule).createNotification(schedule)

        prefs.edit().putInt(LockScreenSDK.KEY_DISPLAY_COUNT_TODAY, displayCountToday + 1).apply()

        val contentsStr = prefs.getString(LockScreenSDK.KEY_JSON_CONTENTS, "[]")
        try {
            val totalContent = org.json.JSONArray(contentsStr).length()
            if (totalContent > 0) {
                var nextIndex = prefs.getInt(LockScreenSDK.KEY_CONTENT_INDEX, 0) + 1
                if (nextIndex >= totalContent) nextIndex = 0 // Quay lại từ đầu nếu hết mảng
                prefs.edit().putInt(LockScreenSDK.KEY_CONTENT_INDEX, nextIndex).apply()
            }
        } catch (e: Exception) {
            Log.e("AlarmManagerImpl", "Lỗi xoay tua Content V2: ${e.message}")
        }

        LockScreenSDK.scheduleDynamicV2Alarm(context, triggerAfterInterval = true)
    }

    fun getWeekOfYearUsingCalendar(timeInMillis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timeInMillis)
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    private fun getCurrentDay(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }

    private fun getCurrentWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    }
}