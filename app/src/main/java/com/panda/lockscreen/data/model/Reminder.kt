package com.panda.lockscreen.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey val id: Int,
    val repeatTimes: Int,
    val timesShowed: Int
)