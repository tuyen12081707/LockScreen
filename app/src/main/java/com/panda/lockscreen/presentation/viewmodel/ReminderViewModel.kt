package com.panda.lockscreen.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panda.lockscreen.R
import com.panda.lockscreen.data.model.Reminder
import com.panda.lockscreen.data.repository.ReminderRepository
import com.panda.lockscreen.notification.AlarmManagerImpl
import com.panda.lockscreen.notification.Schedule
import com.panda.lockscreen.utils.Constants
import com.panda.lockscreen.utils.createDailyReminderSchedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class ReminderViewModel(
    private val repository: ReminderRepository,

) : ViewModel() {

    fun setupReminders(
        context: Context,
        intervals: List<Int>,
        titleIds: List<Int>,
        subTitleIds: List<Int>,
        imageIds: List<Int>
    ) {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)

        intervals.forEachIndexed { index, interval ->
            val schedule = createDailyReminderSchedule(
                intervals = interval,
                hour = currentHour,
                minute = currentMinute,
                units = Constants.TimeUnit.AM,
                titleId = titleIds.getOrNull(index) ?: R.string.reminder_title_1,
                subTitleId = subTitleIds.getOrNull(index) ?: R.string.reminder_subtitle_1,
                imageId = imageIds.getOrNull(index) ?: R.drawable.img_reminder
            )
            setReminder(context, schedule)
        }

    }

    private fun setReminder(context: Context, schedule: Schedule) {
        val alarmManager = AlarmManagerImpl(context)
        alarmManager.cancel(schedule)
        alarmManager.schedule(schedule)
    }


}