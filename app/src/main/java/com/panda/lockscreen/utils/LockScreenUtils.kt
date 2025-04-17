package com.panda.lockscreen.utils

import com.panda.lockscreen.R
import com.panda.lockscreen.notification.Schedule
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.longToDateString(pattern: String = "dd/MM/yyyy HH:mm"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(this))
}


fun createDailyReminderSchedule(
    intervals: Int,
    hour: Int,
    minute: Int,
    createdAt: Long = System.currentTimeMillis(),
    titleId: Int = R.string.unlock_wifi_passwords_near_you,
    subTitleId: Int = R.string.tap_to_check_nearby_wifi_networks_and_connect_effortlessly,
    imageId: Int = R.drawable.img_reminder,
    units: Int = Constants.TimeUnit.AM,
    imageBackup: Int? = null
): Schedule.ScheduleEachDay {


    return Schedule.ScheduleEachDay(
        id = intervals,
        repeatTimes = 0,
        intervals = intervals,
        createdAt = createdAt,
        titleId = titleId,
        subTitleId = subTitleId,
        imageId = imageId,
        hour = hour,
        minute = minute,
        units = units,
        imageBackup = imageBackup
    )
}

/**
 * Reused function to generate a unique ID.
 */
fun generateUniqueId(
    id: Int,
    dayOffset: Int,
): Int {
    val raw = "${id}_${dayOffset}"
    return raw.hashCode()
}