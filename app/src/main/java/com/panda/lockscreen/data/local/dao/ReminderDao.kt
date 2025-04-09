package com.panda.lockscreen.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.panda.lockscreen.data.model.Reminder
import com.panda.lockscreen.notification.Schedule

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Int): Reminder?

    @Query("UPDATE reminders SET timesShowed = :timesShowed WHERE id = :id")
    suspend fun updateTimesShowed(id: Int, timesShowed: Int)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM reminders")
    suspend fun getAll(): List<Reminder>

    @Query("DELETE FROM reminders")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder)
}