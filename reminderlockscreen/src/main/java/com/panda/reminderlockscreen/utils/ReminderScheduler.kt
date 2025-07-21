package com.panda.reminderlockscreen.utils

import android.content.Context
import android.content.Intent
import com.panda.reminderlockscreen.model.LockScreen
import com.panda.reminderlockscreen.notification.AlarmManagerImpl
import com.panda.reminderlockscreen.notification.Schedule
import java.util.Calendar

object ReminderScheduler {

    fun setupReminders(
        context: Context,
        listLockScreen: ArrayList<LockScreen>,
        hour: Int = 0,
        minute: Int = 0,
        isPresent: Boolean = true,
    ) {
        val now = Calendar.getInstance()
        val currentHour: Int
        val currentMinute: Int
        val units: Int

        if (isPresent) {
            currentHour = now.get(Calendar.HOUR_OF_DAY)
            currentMinute = now.get(Calendar.MINUTE)
            units = if (currentHour < 12) Constants.TimeUnit.AM else Constants.TimeUnit.PM
        } else {
            currentHour = hour
            currentMinute = minute
            units = if (hour >= 12) Constants.TimeUnit.PM else Constants.TimeUnit.AM
        }

        val isMonth =
            listLockScreen.any { it.day > 7 || it.type == Constants.TypeLockScreen.TYPE_MONTH }

        if (isMonth) {
            listLockScreen.forEach { item ->
                val schedule = createLockScreenByDayOfMonthSchedule(
                    id = item.id,
                    hour = if (item.hour != -1) item.hour else currentHour,
                    minute = if (item.mintues != -1) item.mintues else currentMinute,
                    units = units,
                    title = item.title,
                    content = item.content,
                    imageUrl = item.image,
                    repeatTimes = item.repeatTimes,
                    days = item.day,
                    buttonContent = item.buttonContent,
                    createdAt = System.currentTimeMillis(),
                    backgroundUrl = item.backgroundUrl,
                    type = item.uiType
                )
                setReminder(context, schedule)
            }
        } else {
            val duplicatedDays = listLockScreen
                .filter { it.day in 1..7 }
                .groupingBy { it.day }
                .eachCount()
                .filter { it.value > 1 }
                .keys

            duplicatedDays.forEach { day ->
                val itemsInDay = listLockScreen.filter { it.day == day }
                val item = itemsInDay.random()

                val schedule = createLockScreenByDayOfWeekSchedule(
                    id = item.id,
                    hour = if (item.hour != -1) item.hour else currentHour,
                    minute = if (item.mintues != -1) item.mintues else currentMinute,
                    units = units,
                    title = item.title,
                    content = item.content,
                    imageUrl = item.image,
                    repeatTimes = item.repeatTimes,
                    buttonContent = item.buttonContent,
                    days = item.day,
                    createdAt = System.currentTimeMillis(),
                    backgroundUrl = item.backgroundUrl,
                    type = item.uiType
                )
                setReminder(context, schedule)
            }

            val uniqueDaysItems = listLockScreen.filter {
                it.day in 1..7 && it.day !in duplicatedDays
            }

            uniqueDaysItems.forEach { item ->
                val schedule = createLockScreenByDayOfWeekSchedule(
                    id = item.id,
                    hour = if (item.hour != -1) item.hour else currentHour,
                    minute = if (item.mintues != -1) item.mintues else currentMinute,
                    units = units,
                    title = item.title,
                    content = item.content,
                    imageUrl = item.image,
                    repeatTimes = item.repeatTimes,
                    buttonContent = item.buttonContent,
                    days = item.day,
                    createdAt = System.currentTimeMillis(),
                    backgroundUrl = item.backgroundUrl,
                    type = item.uiType
                )
                setReminder(context, schedule)
            }
        }
    }

    private fun setReminder(context: Context, schedule: Schedule) {
        val alarmManager = AlarmManagerImpl(context)
        alarmManager.cancel(schedule)
        alarmManager.schedule(schedule)
    }
}