package com.panda.reminderlockscreen.utils

import com.panda.reminderlockscreen.model.DisplayType
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
    backgroundUrl: String, // URL thứ 1
    units: Int = Constants.TimeUnit.AM,
    imageBackup: Int? = null,
    buttonContent: String,
    event: String = "",
    type: Int = 0,
    displayType: String = DisplayType.FULL_SCREEN.name // <-- Thêm trường này để fix đỏ
): Schedule.ScheduleEachDay {
    return Schedule.ScheduleEachDay(
        id = intervals,
        title = title,
        content = content,
        imageUrl = imageUrl, // URL thứ 2
        repeatTimes = 1,
        hour = hour,
        minute = minute,
        units = units,
        buttonContent = buttonContent,
        imageBackup = imageBackup,
        intervals = intervals,
        createdAt = createdAt,
        backgroundUrl = backgroundUrl,
        event = event,
        type = type,
        displayType = displayType // <-- Map vào constructor
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
    backgroundUrl: String, // URL thứ 1
    units: Int = Constants.TimeUnit.AM,
    imageBackup: Int? = null,
    event: String = "",
    type: Int = 0,
    displayType: String = DisplayType.FULL_SCREEN.name // <-- Thêm trường này
): Schedule.ScheduleMonth {
    return Schedule.ScheduleMonth(
        id = id,
        title = title,
        content = content,
        imageUrl = imageUrl, // URL thứ 2
        repeatTimes = repeatTimes,
        hour = hour,
        buttonContent = buttonContent,
        minute = minute,
        backgroundUrl = backgroundUrl,
        dayOfMonth = days,
        units = units,
        imageBackup = imageBackup,
        time = System.currentTimeMillis(),
        event = event,
        type = type,
        displayType = displayType // <-- Map vào constructor
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
    backgroundUrl: String, // URL thứ 1
    repeatTimes: Int = 1,
    units: Int = Constants.TimeUnit.AM,
    imageBackup: Int? = null,
    buttonContent: String,
    event: String = "",
    type: Int = 0,
    displayType: String = DisplayType.FULL_SCREEN.name // <-- Thêm trường này
): Schedule.ScheduleWeek {
    return Schedule.ScheduleWeek(
        id = id,
        title = title,
        content = content,
        imageUrl = imageUrl, // URL thứ 2
        repeatTimes = repeatTimes,
        hour = hour,
        minute = minute,
        dayOfWeek = days,
        units = units,
        buttonContent = buttonContent,
        backgroundUrl = backgroundUrl,
        imageBackup = imageBackup,
        time = System.currentTimeMillis(),
        event = event,
        type = type,
        displayType = displayType // <-- Map vào constructor
    )
}