package com.panda.lockscreen.data.repository

import com.panda.lockscreen.data.local.dao.ReminderDao
import com.panda.lockscreen.data.model.Reminder

class ReminderRepositoryImpl(private val dao: ReminderDao) : ReminderRepository {

    override suspend fun getReminderById(id: Int): Result<Reminder> {
        return try {
            val reminder = dao.getReminderById(id)
            if (reminder != null) Result.success(reminder)
            else Result.failure(Throwable("Reminder not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTimesShowNotification(id: Int, timesShowed: Int): Result<Unit> {
        return try {
            dao.updateTimesShowed(id, timesShowed)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReminderById(id: Int): Result<Unit> {
        return try {
            dao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllReminders(): Result<List<Reminder>> {
        return try {
            Result.success(dao.getAll())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearAllReminders(): Result<Unit> {
        return try {
            dao.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertReminder(reminder: Reminder): Result<Unit> {
        return try {
            dao.insert(reminder)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}