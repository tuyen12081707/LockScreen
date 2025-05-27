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
    open val repeatTimes: Int,
    open val hour: Int,
    open val minute: Int,
    open val units: Int,
    @DrawableRes open val imageBackup: Int? = null,
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
        @DrawableRes override val imageBackup: Int? = null,
        val intervals: Int,
        val createdAt: Long = System.currentTimeMillis()
    ) : Schedule(
        id, title, content, imageUrl, repeatTimes, hour, minute, units, imageBackup
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
        @DrawableRes override val imageBackup: Int? = null,
        val intervals: Int,
        val createdAt: Long = System.currentTimeMillis()
    ) : Schedule(
        id, title, content, imageUrl, repeatTimes, hour, minute, units, imageBackup
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
        @DrawableRes override val imageBackup: Int? = null,
        val time: Long
    ) : Schedule(
        id, title, content, imageUrl, repeatTimes, hour, minute, units, imageBackup
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
        @DrawableRes override val imageBackup: Int? = null,
        val time: Long
    ) : Schedule(
        id, title, content, imageUrl, repeatTimes, hour, minute, units, imageBackup
    )

    @Parcelize
    data class ScheduleMonth(
        override var id: Int,
        override val title: String,
        override val content: String,
        override val imageUrl: String,
        override val repeatTimes: Int,
        override val hour: Int,
        override val minute: Int,
        val dayOfMonth: Int,
        override val units: Int,
        @DrawableRes override val imageBackup: Int? = null,
        val time: Long
    ) : Schedule(
        id, title, content, imageUrl, repeatTimes, hour, minute, units, imageBackup
    )
}
