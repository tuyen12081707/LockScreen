package com.panda.reminderlockscreen.utils

import com.panda.reminderlockscreen.notification.Schedule
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
    imageBackup: Int? = null,
    backgroundUrl:String,
    buttonContent: String,
    event:String="",
    type:Int=0
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
        imageBackup = imageBackup,
        event = event,
        buttonContent = buttonContent,
        backgroundUrl = backgroundUrl,
        type = type
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
    buttonContent: String,
    repeatTimes: Int = 1,
    imageUrl: String,
    backgroundUrl:String,
    units: Int = Constants.TimeUnit.AM,
    imageBackup: Int? = null,
    event: String = "",
    type: Int = 0,
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
        time = System.currentTimeMillis(), event = event,
        buttonContent = buttonContent,
        backgroundUrl = backgroundUrl,
        type = type

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
    backgroundUrl:String,
    repeatTimes: Int = 1,
    units: Int = Constants.TimeUnit.AM,
    imageBackup: Int? = null,
    buttonContent: String,
    event: String = "",
    type: Int = 0,
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
        time = System.currentTimeMillis(),
        event = event,
        buttonContent = buttonContent,
        backgroundUrl = backgroundUrl,
        type = type
    )
}
