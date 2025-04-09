package com.panda.lockscreen.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.panda.lockscreen.utils.Constants
import com.panda.lockscreen.utils.longToDateString
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar


class AlarmManagerImpl(private val context: Context) : AlarmSchedule {
    private val TAG = AlarmManagerImpl::class.simpleName
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(schedule: Schedule) {

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id.hashCode(),
            Intent(context, AlarmReminderBroadcastCast::class.java).apply {
                when (schedule) {
                    is Schedule.ScheduleDay -> {
                        putExtra("schedule_data", schedule)
                    }

                    is Schedule.ScheduleWeek -> {
                        putExtra("schedule_data", schedule)
                    }

                    is Schedule.ScheduleMonth -> {
                        putExtra("schedule_data", schedule)
                    }

                    is Schedule.ScheduleEachDay ->{
                        putExtra("schedule_data", schedule)
                    }
                }
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        when (schedule) {
            is Schedule.ScheduleEachDay -> {
                schedule.intervals.forEach { dayOffset ->
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = schedule.createdAt
                        add(Calendar.DAY_OF_YEAR, dayOffset)
                        set(Calendar.HOUR_OF_DAY, getHourDay(schedule.hour, schedule.units))
                        set(Calendar.MINUTE, schedule.minute)
                        set(Calendar.SECOND, 0)
                    }

                    val intervalId = generateUniqueId(schedule.plantId, dayOffset, calendar.timeInMillis)
                    val intent = Intent(context, AlarmReminderBroadcastCast::class.java).apply {
                        putExtra("schedule_data", schedule.copy(id = intervalId)) // 🔄 copy để đổi ID
                    }

                    val intervalPendingIntent = PendingIntent.getBroadcast(
                        context,
                        intervalId.hashCode(),
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                intervalPendingIntent
                            )
                        } else {
                            alarmManager.set(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                intervalPendingIntent
                            )
                        }
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            intervalPendingIntent
                        )
                    }

                    Log.e(TAG, "ScheduleEachDay: Set reminder at ${calendar.timeInMillis.longToDateString("dd/MM/yy HH:mm")} (after $dayOffset days)")
                }
            }

            is Schedule.ScheduleDay -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, getHourDay(schedule.hour, schedule.units))
                calendar.set(Calendar.MINUTE, schedule.minute)
                calendar.set(Calendar.SECOND, 0)
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                )
                Log.e(TAG, "ScheduleDay:  ${calendar.timeInMillis.longToDateString("dd/MM/yy  HH:mm")} ${schedule.hour} : ${schedule.minute}")
            }

            is Schedule.ScheduleWeek -> {
                val calendar = Calendar.getInstance()
                try {
                    calendar.set(Calendar.DAY_OF_WEEK, schedule.dayOfWeek)
                } catch (_: Exception) {
                }
                calendar.set(
                    Calendar.HOUR_OF_DAY,
                    getHourDay(schedule.hour, schedule.units)
                )
                calendar.set(Calendar.MINUTE, schedule.minute)
                calendar.set(Calendar.SECOND, 0)
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.e(
                    TAG,
                    "ScheduleWeek: ${getCurrentWeek()}  ${
                        getWeekOfYearUsingCalendar(schedule.time)
                    }",
                )
                Log.e(
                    TAG,
                    "ScheduleWeek: ${calendar.timeInMillis.longToDateString("dd/MM/yy  HH:mm")}  ${schedule.hour} : ${schedule.minute} "
                )
            }

            is Schedule.ScheduleMonth -> {
                val calendar = Calendar.getInstance()
                val currentMonth = calendar.get(Calendar.MONTH)
                calendar.set(Calendar.DAY_OF_MONTH, schedule.dayOfMonth)
                calendar.set(Calendar.HOUR_OF_DAY, getHourDay(schedule.hour, schedule.units))
                calendar.set(Calendar.MINUTE, schedule.minute)
                calendar.set(Calendar.SECOND, 0)
                when (currentMonth) {
                    Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> {
                        if (calendar.timeInMillis < System.currentTimeMillis()) {
                            calendar.add(Calendar.DATE, 31)
                        }
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }

                    Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> {
                        if (calendar.timeInMillis < System.currentTimeMillis()) {
                            calendar.add(Calendar.DATE, 30)
                        }
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }

                    Calendar.FEBRUARY -> {
                        val cal: GregorianCalendar =
                            GregorianCalendar.getInstance() as GregorianCalendar
                        if (cal.isLeapYear(calendar.get(Calendar.YEAR))) { //for leap year feburary month
                            if (calendar.timeInMillis < System.currentTimeMillis()) {
                                calendar.add(Calendar.DATE, 29)
                            }
                            alarmManager.setAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                pendingIntent
                            )
                        } else { //for non leap year feburary month
                            if (calendar.timeInMillis < System.currentTimeMillis()) {
                                calendar.add(Calendar.DATE, 28)
                            }
                            alarmManager.setAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                pendingIntent
                            )
                        }
                    }
                }
                Log.e(TAG, "ScheduleMonth:  ${calendar.timeInMillis.longToDateString("dd/MM/yy  HH:mm")}  ${schedule.hour} : ${schedule.minute}")
            }
        }

    }

    fun generateUniqueId(
        plantId: Int,
        dayOffset: Int,
        baseTime: Long = System.currentTimeMillis()
    ): Int {
        val raw = "${plantId}_${dayOffset}_$baseTime"
        return raw.hashCode()
    }
    private fun getHourDay(hour: Int, unit: Int): Int {
        return if (unit == Constants.TimeUnit.AM) {
            hour
        } else {
            hour + 12
        }
    }

    private fun getWeekOfYearUsingCalendar(timeInMillis: Long): Int {
        // Create a Calendar instance
        val calendar = Calendar.getInstance()

        // Set the calendar time to the provided timestamp
        calendar.time = Date(timeInMillis)

        // Get the week of year
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    private fun getCurrentWeek(): Int {
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
    }

    override fun cancel(schedule: Schedule) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                schedule.id.toInt(),
                Intent(context, AlarmReminderBroadcastCast::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}