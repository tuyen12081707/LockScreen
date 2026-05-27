package com.panda.reminderlockscreen.notification

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import java.util.Calendar

object LockScreenSDK {

    private const val TAG = "LockScreenSDK"
    private const val PREF_NAME = "LockScreenSDK_Prefs"
    private const val TARGET_ACTIVITY_KEY = "TARGET_ACTIVITY_CLASS"

    fun init(
        context: Context,
        remoteConfigJson: String,
        targetActivity: Class<*>
    ) {
        if (remoteConfigJson.isBlank()) {
            Log.e(TAG, "Chuỗi JSON trống, không thể lên lịch!")
            return
        }

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(TARGET_ACTIVITY_KEY, targetActivity.name)
            .apply()

        val schedules = parseJsonToSchedules(remoteConfigJson)

        if (schedules.isEmpty()) {
            Log.e(TAG, "Không có lịch trình nào được tạo (Lỗi Parse JSON).")
            return
        }

        val alarmManager = AlarmManagerImpl(context)
        schedules.forEach { schedule ->
            Log.d(
                TAG,
                "Đang lên lịch cho: ${schedule.title} - Loại: ${schedule.javaClass.simpleName}"
            )
            // FIX 2: Bổ sung logic Hủy báo thức cũ trước khi set cái mới
            alarmManager.cancel(schedule)
            alarmManager.schedule(schedule)
        }
    }

    private fun parseJsonToSchedules(jsonString: String): List<Schedule> {
        val list = mutableListOf<Schedule>()
        try {
            // FIX 1: Lấy giờ phút hiện tại để dự phòng (Fallback)
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

                // Lấy giờ phút từ JSON, nếu Firebase truyền -1 thì lấy giờ hiện tại
                val jsonHour = item.optInt("hour", 0)
                val jsonMinute = item.optInt("minutes", 0)

                val hour = if (jsonHour != -1) jsonHour else currentHour
                val minute = if (jsonMinute != -1) jsonMinute else currentMinute

                // Tự động tính toán AM/PM dựa vào giờ cuối cùng
                val units = if (hour < 12) 0 else 1 // Giả định 0 là AM, 1 là PM theo logic của ông

                val buttonContent = item.optString("buttonContent", "Bắt đầu")
                val type = item.optString("type", "")
                val repeatTimes = item.optInt("repeatTimes", 1)
                val event = item.optString("event", "")
                val timeNow = System.currentTimeMillis()

                val schedule = when (type.lowercase()) {
                    "week" -> Schedule.ScheduleWeek(
                        id = id,
                        title = title,
                        content = content,
                        imageUrl = imageUrl,
                        backgroundUrl = backgroundUrl,
                        repeatTimes = repeatTimes,
                        hour = hour,
                        minute = minute,
                        dayOfWeek = day,
                        units = units,
                        buttonContent = buttonContent,
                        time = timeNow,
                        event = event,
                        type = 1
                    )

                    "month" -> Schedule.ScheduleMonth(
                        id = id,
                        title = title,
                        content = content,
                        imageUrl = imageUrl,
                        backgroundUrl = backgroundUrl,
                        repeatTimes = repeatTimes,
                        hour = hour,
                        minute = minute,
                        dayOfMonth = day,
                        units = units,
                        buttonContent = buttonContent,
                        time = timeNow,
                        event = event,
                        type = 2
                    )

                    "day" -> Schedule.ScheduleDay(
                        id = id, title = title, content = content, imageUrl = imageUrl,
                        backgroundUrl = backgroundUrl, repeatTimes = repeatTimes, hour = hour,
                        minute = minute, units = units, buttonContent = buttonContent,
                        time = timeNow, event = event, type = 3
                    )

                    "each_day" -> Schedule.ScheduleEachDay(
                        id = id, title = title, content = content, imageUrl = imageUrl,
                        backgroundUrl = backgroundUrl, repeatTimes = repeatTimes, hour = hour,
                        minute = minute, units = units, buttonContent = buttonContent,
                        intervals = intervals, createdAt = timeNow, event = event, type = 4
                    )

                    "remote" -> Schedule.ScheduleRemote(
                        id = id, title = title, content = content, imageUrl = imageUrl,
                        backgroundUrl = backgroundUrl, repeatTimes = repeatTimes, hour = hour,
                        minute = minute, units = units, buttonContent = buttonContent,
                        intervals = intervals, createdAt = timeNow, event = event, type = 5
                    )

                    else -> {
                        Log.e(TAG, "Type không hợp lệ trong JSON: $type")
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