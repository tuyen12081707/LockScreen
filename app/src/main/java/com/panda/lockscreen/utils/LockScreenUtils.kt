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
    title: String,
    content: String,
    imageUrl: String,
    units: Int = Constants.TimeUnit.AM,
    imageBackup: Int? = null
): Schedule.ScheduleEachDay {
    return Schedule.ScheduleEachDay(
        id = intervals,
        repeatTimes = 1,
        intervals = intervals,
        createdAt = createdAt,
        title = title,
        content = content,
        imageUrl = imageUrl,
        hour = hour,
        minute = minute,
        units = units,
        imageBackup = imageBackup
    )
}



fun createLockScreenByDayOfMonthSchedule(
    id: Int,
    days: Int,
    hour: Int,
    minute: Int,
    createdAt: Long = System.currentTimeMillis(),
    title: String,
    content: String,
    repeatTimes: Int = 1,
    imageUrl: String,
    units: Int = Constants.TimeUnit.AM,
    imageBackup: Int? = null
): Schedule.ScheduleMonth {
    return Schedule.ScheduleMonth(
        id = id,
        repeatTimes = repeatTimes,
        title = title,
        content = content,
        imageUrl = imageUrl,
        hour = hour,
        minute = minute,
        units = units,
        imageBackup = imageBackup,
        dayOfMonth = days,
        time = System.currentTimeMillis()
    )
}

fun createLockScreenByDayOfWeekSchedule(
    id: Int,
    days: Int,
    hour: Int,
    minute: Int,
    createdAt: Long = System.currentTimeMillis(),
    title: String,
    content: String,
    imageUrl: String,
    repeatTimes: Int = 1,
    units: Int = Constants.TimeUnit.AM,
    imageBackup: Int? = null
): Schedule.ScheduleWeek {
    return Schedule.ScheduleWeek(
        id = id,
        repeatTimes = repeatTimes,
        title = title,
        content = content,
        imageUrl = imageUrl,
        hour = hour,
        minute = minute,
        units = units,
        imageBackup = imageBackup,
        dayOfWeek = days,
        time = System.currentTimeMillis()
    )
}
