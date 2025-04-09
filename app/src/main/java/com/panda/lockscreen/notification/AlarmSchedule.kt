package com.panda.lockscreen.notification

interface AlarmSchedule {
    fun schedule(schedule: Schedule)
    fun cancel(schedule: Schedule)
}