package com.panda.lockscreen.di

import android.app.AlarmManager
import android.content.Context
import androidx.room.Room
import com.panda.lockscreen.data.local.db.AppDatabase
import com.panda.lockscreen.data.repository.ReminderRepository
import com.panda.lockscreen.data.repository.ReminderRepositoryImpl
import com.panda.lockscreen.notification.AlarmManagerImpl
import com.panda.lockscreen.notification.AlarmSchedule
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
    val databaseModule = module {
        single { AppDatabase.getInstance(get()) }
        single { get<AppDatabase>().reminderDao() }
        single<ReminderRepository> { ReminderRepositoryImpl(get()) }
    }

