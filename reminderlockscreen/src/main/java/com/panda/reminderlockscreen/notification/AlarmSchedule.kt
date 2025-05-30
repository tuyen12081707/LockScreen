package com.panda.reminderlockscreen.notification

interface AlarmSchedule {
    fun schedule(schedule: Schedule)
    fun cancel(schedule: Schedule)
}