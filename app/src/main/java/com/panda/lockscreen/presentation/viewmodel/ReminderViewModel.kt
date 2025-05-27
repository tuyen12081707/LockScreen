package com.panda.lockscreen.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panda.lockscreen.R
import com.panda.lockscreen.data.model.LockScreen
import com.panda.lockscreen.data.model.Reminder
import com.panda.lockscreen.data.repository.ReminderRepository
import com.panda.lockscreen.notification.AlarmManagerImpl
import com.panda.lockscreen.notification.Schedule
import com.panda.lockscreen.utils.Constants
import com.panda.lockscreen.utils.createDailyReminderSchedule
import com.panda.lockscreen.utils.createLockScreenByDayOfMonthSchedule
import com.panda.lockscreen.utils.createLockScreenByDayOfWeekSchedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class ReminderViewModel() : ViewModel() {

    fun setupReminders(
        context: Context,
        hour: Int = 0,
        minute: Int = 0,
        listLockScreen: ArrayList<LockScreen>,
        isPresent: Boolean = true
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
        val isMonth = listLockScreen.any { it.day > 7 }
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
                    createdAt = System.currentTimeMillis()
                )

                setReminder(context, schedule)

            }
        } else {
            /**
             * Xử lý days bị trùng trong list
             */
            val duplicatedDays = listLockScreen
                .filter { it.day in 1..7 }
                .groupingBy { it.day }
                .eachCount()
                .filter { it.value > 1 }
                .keys

            duplicatedDays.forEach { day ->
                val itemsInDay = listLockScreen.filter { it.day == day  }
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
                    days = item.day,
                    createdAt = System.currentTimeMillis()
                )
                setReminder(context, schedule)
            }
            /*
            Xử lý các ngày không bị trùng
             */
            val uniqueDaysItems = listLockScreen.filter {
                it.day in 1..7 &&
                        it.day !in duplicatedDays
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
                    days = item.day,
                    createdAt = System.currentTimeMillis()
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