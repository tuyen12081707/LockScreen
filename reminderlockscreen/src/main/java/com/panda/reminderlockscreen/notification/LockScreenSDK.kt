package com.panda.reminderlockscreen.notification

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONException

object LockScreenSDK {

    private const val TAG = "LockScreenSDK"
    private const val PREF_NAME = "LockScreenSDK_Prefs"
    private const val TARGET_ACTIVITY_KEY = "TARGET_ACTIVITY_CLASS"

    /**
     * Khởi tạo SDK.
     * @param context Context của App
     * @param remoteConfigJson Chuỗi JSON cấu hình lấy từ Firebase (hoặc API)
     * @param targetActivity Class của màn hình muốn mở khi user bấm "Bắt đầu"
     */
    fun init(
        context: Context,
        remoteConfigJson: String,
        targetActivity: Class<*>
    ) {
        if (remoteConfigJson.isBlank()) {
            Log.e(TAG, "Chuỗi JSON trống, không thể lên lịch!")
            return
        }

        // 1. Lưu lại Activity đích để mở sau này
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(TARGET_ACTIVITY_KEY, targetActivity.name)
            .apply()

        // 2. Parse JSON thành List<Schedule>
        val schedules = parseJsonToSchedules(remoteConfigJson)

        if (schedules.isEmpty()) {
            Log.e(TAG, "Không có lịch trình nào được tạo (Lỗi Parse JSON).")
            return
        }

        // 3. Gọi báo thức
        val alarmManager = AlarmManagerImpl(context)
        schedules.forEach { schedule ->
            Log.d(TAG, "Đang lên lịch cho: ${schedule.title} - Loại: ${schedule.javaClass.simpleName}")
            alarmManager.schedule(schedule)
        }
    }

    private fun parseJsonToSchedules(jsonString: String): List<Schedule> {
        val list = mutableListOf<Schedule>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                val id = item.optInt("id", 0)
                val title = item.optString("title", "")
                val content = item.optString("content", "")
                val backgroundUrl = item.optString("backgroundUrl", "")
                val imageUrl = item.optString("image", "")
                val day = item.optInt("day", 1) // Ngày trong tuần hoặc ngày trong tháng
                val hour = item.optInt("hour", 0)
                val minute = item.optInt("minutes", 0)
                val buttonContent = item.optString("buttonContent", "Bắt đầu")
                val type = item.optString("type", "")
                val repeatTimes = item.optInt("repeatTimes", 1)
                val event = item.optString("event", "")
                val timeNow = System.currentTimeMillis()

                val schedule = when (type.lowercase()) {
                    "week" -> Schedule.ScheduleWeek(
                        id = id, title = title, content = content, imageUrl = imageUrl,
                        backgroundUrl = backgroundUrl, repeatTimes = repeatTimes, hour = hour,
                        minute = minute, dayOfWeek = day, units = 0, buttonContent = buttonContent,
                        time = timeNow, event = event, type = 1 // Giả định type=1 là week
                    )
                    "month" -> Schedule.ScheduleMonth(
                        id = id, title = title, content = content, imageUrl = imageUrl,
                        backgroundUrl = backgroundUrl, repeatTimes = repeatTimes, hour = hour,
                        minute = minute, dayOfMonth = day, units = 0, buttonContent = buttonContent,
                        time = timeNow, event = event, type = 2 // Giả định type=2 là month
                    )
                    else -> {
                        Log.e(TAG, "Type không hợp lệ: $type")
                        null
                    }
                }
                schedule?.let { list.add(it) }
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Lỗi khi parse JSON: ${e.message}")
        }
        return list
    }
}