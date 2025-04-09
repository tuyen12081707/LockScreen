package com.panda.lockscreen.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panda.lockscreen.data.model.Reminder
import com.panda.lockscreen.data.repository.ReminderRepository
import com.panda.lockscreen.notification.AlarmManagerImpl
import com.panda.lockscreen.notification.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val repository: ReminderRepository,

) : ViewModel() {

    fun setReminder(context: Context,schedule: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = schedule.id.takeIf { it != 0 } ?: generateId(schedule)
            val reminderToSave = Reminder(
                id = id,
                plantId = schedule.plantId,
                repeatTimes = schedule.repeatTimes,
                timesShowed = 0
            )

            repository.deleteReminderById(reminderToSave.id)

            repository.insertReminder(reminderToSave)

            AlarmManagerImpl(context).schedule(schedule)
        }
    }

    private fun generateId(schedule: Schedule): Int {
        val time = when (schedule) {
            is Schedule.ScheduleDay -> schedule.time
            is Schedule.ScheduleWeek -> schedule.time
            is Schedule.ScheduleMonth -> schedule.time
            is Schedule.ScheduleEachDay -> schedule.toString()
        }
        return (schedule.plantId.toString() + time.toString()).hashCode()
    }
}