package com.panda.reminderlockscreen.notification

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.panda.reminderlockscreen.model.DisplayType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar

object LockScreenSDK {
    const val TAG = "LockScreenSDK"
    const val PREF_NAME = "LockScreenSDK_Prefs"

    const val KEY_TARGET_ACTIVITY = "TARGET_ACTIVITY_CLASS"
    const val KEY_ACTIVE_VERSION = "active_version"

    // Keys cho V2
    const val KEY_ENABLED = "sdk_enabled"
    const val KEY_MAX_DISPLAY = "max_display_per_day"
    const val KEY_DISPLAY_COUNT_TODAY = "display_count_today"
    const val KEY_LAST_DISPLAY_DAY = "last_display_day"
    const val KEY_CONTENT_INDEX = "current_content_index"
    const val KEY_JSON_CONTENTS = "json_contents_array"
    const val KEY_TARGET_HOURS = "target_hours_array"
    const val KEY_INTERVAL_MINUTES = "repeat_interval_minutes"

    fun init(context: Context, remoteConfigJson: String, targetActivity: Class<*>) {
        if (remoteConfigJson.isBlank()) {
            Log.e(TAG, "Chuỗi JSON trống!")
            return
        }

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_TARGET_ACTIVITY, targetActivity.name) }

        val jsonTrimmed = remoteConfigJson.trim()

        CoroutineScope(Dispatchers.IO).launch {
            if (jsonTrimmed.startsWith("{")) {
                Log.d(TAG, "Init V2 (Object)")
                prefs.edit { putInt(KEY_ACTIVE_VERSION, 2) }
                initV2(context, jsonTrimmed, prefs)
            } else if (jsonTrimmed.startsWith("[")) {
                Log.d(TAG, "Init V1 (Array)")
                prefs.edit { putInt(KEY_ACTIVE_VERSION, 1) }
                initV1(context, jsonTrimmed)
            } else {
                Log.e(TAG, "Định dạng JSON không hợp lệ!")
            }
        }
    }

    // ==========================================
    // LOGIC V1 (MẢNG CŨ)
    // ==========================================
    private fun initV1(context: Context, jsonString: String) {
        val schedules = parseJsonToSchedulesV1(jsonString)
        val alarmManager = AlarmManagerImpl(context)
        schedules.forEach { schedule ->
            alarmManager.cancel(schedule)
            alarmManager.schedule(schedule)
        }
    }

    private fun parseJsonToSchedulesV1(jsonString: String): List<Schedule> {
        val list = mutableListOf<Schedule>()
        try {
            val now = Calendar.getInstance()
            val currentHour = now.get(Calendar.HOUR_OF_DAY)
            val currentMinute = now.get(Calendar.MINUTE)

            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                val id = item.optInt("id", 0)
                val title = item.optString("title", "")
                val content = item.optString("content", "")
                val backgroundUrl = item.optString("backgroundUrl", "")
                val imageUrl = item.optString("image", "")
                val day = item.optInt("day", 1)
                val intervals = item.optInt("intervals", 0)

                val jsonHour = item.optInt("hour", 0)
                val jsonMinute = item.optInt("minutes", 0)
                val hour = if (jsonHour != -1) jsonHour else currentHour
                val minute = if (jsonMinute != -1) jsonMinute else currentMinute
                val units = if (hour < 12) 0 else 1

                val buttonContent = item.optString("buttonContent", "Bắt đầu")
                val type = item.optString("type", "")
                val repeatTimes = item.optInt("repeatTimes", 1)
                val event = item.optString("event", "")
                val timeNow = System.currentTimeMillis()

                // 1. Đọc trường displayType từ JSON. Mặc định là FULL_SCREEN nếu không có.
                val displayTypeStr = item.optString("displayType", DisplayType.FULL_SCREEN.name)

                // 2. Sử dụng Named Arguments để tránh nhầm lẫn vị trí tham số
                val schedule = when (type.lowercase()) {
                    "week" -> Schedule.ScheduleWeek(
                        id = id,
                        title = title,
                        content = content,
                        imageUrl = imageUrl,
                        repeatTimes = repeatTimes,
                        hour = hour,
                        minute = minute,
                        dayOfWeek = day,
                        units = units,
                        buttonContent = buttonContent,
                        backgroundUrl = backgroundUrl,
                        imageBackup = null,
                        time = timeNow,
                        event = event,
                        type = 1,
                        displayType = displayTypeStr
                    )

                    "month" -> Schedule.ScheduleMonth(
                        id = id,
                        title = title,
                        content = content,
                        imageUrl = imageUrl,
                        repeatTimes = repeatTimes,
                        hour = hour,
                        buttonContent = buttonContent,
                        minute = minute,
                        backgroundUrl = backgroundUrl,
                        dayOfMonth = day,
                        units = units,
                        imageBackup = null,
                        time = timeNow,
                        event = event,
                        type = 2,
                        displayType = displayTypeStr
                    )

                    "day" -> Schedule.ScheduleDay(
                        id = id,
                        title = title,
                        content = content,
                        imageUrl = imageUrl,
                        repeatTimes = repeatTimes,
                        hour = hour,
                        minute = minute,
                        units = units,
                        backgroundUrl = backgroundUrl,
                        buttonContent = buttonContent,
                        imageBackup = null,
                        time = timeNow,
                        event = event,
                        type = 3,
                        displayType = displayTypeStr
                    )

                    "each_day" -> Schedule.ScheduleEachDay(
                        id = id,
                        title = title,
                        content = content,
                        imageUrl = imageUrl,
                        repeatTimes = repeatTimes,
                        hour = hour,
                        minute = minute,
                        units = units,
                        buttonContent = buttonContent,
                        imageBackup = null,
                        intervals = intervals,
                        createdAt = timeNow,
                        backgroundUrl = backgroundUrl,
                        event = event,
                        type = 4,
                        displayType = displayTypeStr
                    )

                    "remote" -> Schedule.ScheduleRemote(
                        id = id,
                        title = title,
                        content = content,
                        imageUrl = imageUrl,
                        repeatTimes = repeatTimes,
                        hour = hour,
                        minute = minute,
                        units = units,
                        buttonContent = buttonContent,
                        backgroundUrl = backgroundUrl,
                        imageBackup = null,
                        intervals = intervals,
                        createdAt = timeNow,
                        event = event,
                        type = 5,
                        displayType = displayTypeStr
                    )

                    else -> null
                }
                schedule?.let { list.add(it) }
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Lỗi Parse V1: ${e.message}")
        }
        return list
    }

    // ==========================================
    // LOGIC V2 (OBJECT MỚI)
    // ==========================================
    private fun initV2(
        context: Context,
        jsonString: String,
        prefs: SharedPreferences
    ) {
        try {
            val root = JSONObject(jsonString)
            val enabled = root.optBoolean("enabled", true)
            val maxDisplay = root.optInt("max_display_per_day", 5)
            val intervalMinutes = root.optInt("repeat_interval_minutes", 60)
            val targetHoursArray = root.optJSONArray("target_hours")
            val contentsArray = root.optJSONArray("contents")

            if (contentsArray == null || contentsArray.length() == 0) return

            prefs.edit().apply {
                putBoolean(KEY_ENABLED, enabled)
                putInt(KEY_MAX_DISPLAY, maxDisplay)
                putInt(KEY_INTERVAL_MINUTES, intervalMinutes)
                putString(KEY_JSON_CONTENTS, contentsArray.toString())
                putString(KEY_TARGET_HOURS, targetHoursArray?.toString() ?: "[]")
                apply()
            }

            if (!enabled) return
            scheduleDynamicV2Alarm(context, triggerAfterInterval = false)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi Init V2: ${e.message}")
        }
    }

    fun scheduleDynamicV2Alarm(context: Context, triggerAfterInterval: Boolean = false) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_ENABLED, true)) return

        val alarmManager = AlarmManagerImpl(context)
        val nextCalendar = Calendar.getInstance()

        if (triggerAfterInterval) {
            val interval = prefs.getInt(KEY_INTERVAL_MINUTES, 60)
            nextCalendar.add(Calendar.MINUTE, interval)
        } else {
            val targetHoursStr = prefs.getString(KEY_TARGET_HOURS, "[]")
            val hoursList = mutableListOf<Int>()
            try {
                val jsonArray = JSONArray(targetHoursStr)
                for (i in 0 until jsonArray.length()) hoursList.add(jsonArray.getInt(i))
            } catch (e: Exception) {
            }

            hoursList.sort()

            val currentHour = nextCalendar.get(Calendar.HOUR_OF_DAY)
            var targetHour = -1
            for (h in hoursList) {
                if (h > currentHour) {
                    targetHour = h
                    break
                }
            }

            if (targetHour != -1) {
                nextCalendar.set(Calendar.HOUR_OF_DAY, targetHour)
                nextCalendar.set(Calendar.MINUTE, 0)
                nextCalendar.set(Calendar.SECOND, 0)
            } else {
                val firstHour = hoursList.firstOrNull() ?: 8
                nextCalendar.add(Calendar.DAY_OF_YEAR, 1)
                nextCalendar.set(Calendar.HOUR_OF_DAY, firstHour)
                nextCalendar.set(Calendar.MINUTE, 0)
                nextCalendar.set(Calendar.SECOND, 0)
            }
        }

        val contentsStr = prefs.getString(KEY_JSON_CONTENTS, "[]")
        try {
            val jsonArray = JSONArray(contentsStr)
            var index = prefs.getInt(KEY_CONTENT_INDEX, 0)
            if (index >= jsonArray.length()) index = 0

            val item = jsonArray.getJSONObject(index)
            val triggerTime = nextCalendar.timeInMillis
            val displayTypeStr = item.optString("displayType", DisplayType.FULL_SCREEN.name)
            val nextSchedule = Schedule.ScheduleDay(
                id = item.optInt("id", 999),
                title = item.optString("title"),
                content = item.optString("content"),
                imageUrl = item.optString("image"),
                backgroundUrl = item.optString("backgroundUrl"),
                buttonContent = item.optString("buttonContent"),
                repeatTimes = 0,
                hour = nextCalendar.get(Calendar.HOUR_OF_DAY),
                minute = nextCalendar.get(Calendar.MINUTE),
                units = 0,
                time = triggerTime,
                event = item.optString("event"),
                type = 3,
                displayType = displayTypeStr
            )

            alarmManager.cancel(nextSchedule)
            alarmManager.schedule(nextSchedule)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi tạo Schedule V2: ${e.message}")
        }
    }
    fun updateTargetActivity(context: Context, targetActivity: Class<*>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_TARGET_ACTIVITY, targetActivity.name) }

    }


    fun addSchedule(context: Context, schedule: Schedule, targetActivity: Class<*>? = null) {
        targetActivity?.let {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit { putString(KEY_TARGET_ACTIVITY, targetActivity.name) }
        }
        try {

            val alarmManager = AlarmManagerImpl(context)
            alarmManager.cancel(schedule)

            alarmManager.schedule(schedule)

            Log.d(
                TAG,
                "Đã ADD thành công Schedule Local: ID = ${schedule.id}, Title = ${schedule.title}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi add Schedule Local: ${e.message}")
        }
    }

    fun cancelSchedule(context: Context, schedule: Schedule) {
        try {
            val alarmManager = AlarmManagerImpl(context)
            alarmManager.cancel(schedule)
            Log.d(TAG, "Đã HỦY Schedule Local: ID = ${schedule.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi hủy Schedule Local: ${e.message}")
        }
    }


}