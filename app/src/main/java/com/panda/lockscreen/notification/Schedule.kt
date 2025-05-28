package com.panda.lockscreen.notification

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Schedule(
    open var id: Int,
    open val title: String,
    open val content: String,
    open val imageUrl: String,
    open val backgroundUrl:String,
    open val buttonContent: String,
    open val repeatTimes: Int,
    open val hour: Int,
    open val minute: Int,
    open val units: Int,
    @DrawableRes open val imageBackup: Int? = null,
    open val event:String,
) : Parcelable {

    @Parcelize
    data class ScheduleRemote(
        override var id: Int,
        override val title: String,
        override val content: String,
        override val imageUrl: String,
        override val repeatTimes: Int,
        override val hour: Int,
        override val minute: Int,
        override val units: Int,
        override val buttonContent: String,
        override val backgroundUrl: String,
        @DrawableRes override val imageBackup: Int? = null,
        val intervals: Int,
        val createdAt: Long = System.currentTimeMillis(),
        override val event:String
    ) : Schedule(
        id, title, content, imageUrl,backgroundUrl, buttonContent = buttonContent, repeatTimes, hour, minute, units, imageBackup,event
    )

    @Parcelize
    data class ScheduleEachDay(
        override var id: Int,
        override val title: String,
        override val content: String,
        override val imageUrl: String,
        override val repeatTimes: Int,
        override val hour: Int,
        override val minute: Int,
        override val units: Int,
        override val buttonContent: String,
        @DrawableRes override val imageBackup: Int? = null,
        val intervals: Int,
        val createdAt: Long = System.currentTimeMillis(),
        override val backgroundUrl: String,
        override val event: String,
    ): Schedule(
        id, title, content, imageUrl,backgroundUrl, buttonContent = buttonContent, repeatTimes, hour, minute, units, imageBackup,event
    )

    @Parcelize
    data class ScheduleDay(
        override var id: Int,
        override val title: String,
        override val content: String,
        override val imageUrl: String,
        override val repeatTimes: Int,
        override val hour: Int = 0,
        override val minute: Int = 0,
        override val units: Int = 0,
        override val backgroundUrl: String,
        override val buttonContent: String,
        @DrawableRes override val imageBackup: Int? = null,
        val time: Long,
        override val event: String,
    ): Schedule(
        id, title, content, imageUrl,backgroundUrl, buttonContent = buttonContent, repeatTimes, hour, minute, units, imageBackup,event
    )

    @Parcelize
    data class ScheduleWeek(
        override var id: Int,
        override val title: String,
        override val content: String,
        override val imageUrl: String,
        override val repeatTimes: Int,
        override val hour: Int,
        override val minute: Int,
        val dayOfWeek: Int,
        override val units: Int,
        override val buttonContent: String,
        override val backgroundUrl: String,
        @DrawableRes override val imageBackup: Int? = null,
        val time: Long,
        override val event: String,
    ) : Schedule(
        id, title, content, imageUrl,backgroundUrl, buttonContent = buttonContent, repeatTimes, hour, minute, units, imageBackup,event
    )

    @Parcelize
    data class ScheduleMonth(
        override var id: Int,
        override val title: String,
        override val content: String,
        override val imageUrl: String,
        override val repeatTimes: Int,
        override val hour: Int,
        override val buttonContent: String,
        override val minute: Int,
        override val backgroundUrl: String,
        val dayOfMonth: Int,
        override val units: Int,
        @DrawableRes override val imageBackup: Int? = null,
        val time: Long,
        override val event: String,
    ) : Schedule(
        id, title, content, imageUrl,backgroundUrl, buttonContent = buttonContent, repeatTimes, hour, minute, units, imageBackup,event
    )
}
