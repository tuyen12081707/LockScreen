package com.panda.lockscreen.data.repository

import com.panda.lockscreen.data.model.Reminder
import com.panda.lockscreen.notification.Schedule

interface ReminderRepository {
    suspend fun getReminderById(id: Int): Result<Reminder>
    suspend fun updateTimesShowNotification(id: Int, timesShowed: Int): Result<Unit>
    suspend fun deleteReminderById(id: Int): Result<Unit>
    suspend fun getAllReminders(): Result<List<Reminder>>
    suspend fun clearAllReminders(): Result<Unit>
    suspend fun insertReminder(reminder: Reminder): Result<Unit>

}