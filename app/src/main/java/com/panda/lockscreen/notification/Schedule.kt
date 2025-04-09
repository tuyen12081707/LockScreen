package com.panda.lockscreen.notification

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
sealed class Schedule(
    open var id: Int,
    open val plantId: Int,
    open val plantThumb: String,
    open val plantName: String,
    open val about: Int,
    open val repeatTimes: Int,
    open val hour: Int,
    open val minute: Int,
    open val units: Int,
    @DrawableRes open val imageBackup: Int? = null
) : Parcelable {
    @Parcelize
    data class ScheduleEachDay(
        override var id: Int,
        override var plantId: Int,
        override val plantThumb: String,
        override val plantName: String,
        override val about: Int,
        override val repeatTimes: Int,
        override val hour: Int,
        override val minute: Int,
        override val units: Int,
        @DrawableRes override var imageBackup: Int? = null,
        val intervals: List<Int>, // ✅ các mốc ngày sau (vd: [3, 7, 10])
        val createdAt: Long = System.currentTimeMillis() // ✅ ngày bắt đầu (mốc tính)
    ) : Schedule(
        id,
        plantId,
        plantThumb,
        plantName,
        about,
        repeatTimes,
        hour,
        minute,
        units,
        imageBackup
    )
    @Parcelize
    data class ScheduleDay(
        override var id: Int,
        override var plantId: Int,
        override val plantThumb: String,
        override val plantName: String,
        override val about: Int,
        override val repeatTimes: Int,
        override val hour: Int,
        override val minute: Int,
        override val units: Int,
        @DrawableRes override var imageBackup: Int? = null,
        val time: Long,
    ) :
        Schedule(
            id,
            plantId,
            plantThumb,
            plantName,
            about,
            repeatTimes,
            hour,
            minute,
            units,
            imageBackup
        )

    @Parcelize
    data class ScheduleWeek(
        override var id: Int,
        override var plantId: Int,
        override val plantThumb: String,
        override val plantName: String,
        override val about: Int,
        override val repeatTimes: Int,
        override val hour: Int,
        override val minute: Int,
        val dayOfWeek: Int,
        override val units: Int,
        @DrawableRes override var imageBackup: Int? = null,
        val time: Long,
    ) :
        Schedule(
            id,
            plantId,
            plantThumb,
            plantName,
            about,
            repeatTimes,
            hour,
            minute,
            units,
            imageBackup
        )

    @Parcelize
    data class ScheduleMonth(
        override var id: Int,
        override var plantId: Int,
        override val plantThumb: String,
        override val plantName: String,
        override val about: Int,
        override val repeatTimes: Int,
        override val hour: Int,
        override val minute: Int,
        val dayOfMonth: Int,
        override val units: Int,
        @DrawableRes override var imageBackup: Int? = null,
        val time: Long,
    ) :
        Schedule(
            id,
            plantId,
            plantThumb,
            plantName,
            about,
            repeatTimes,
            hour,
            minute,
            units,
            imageBackup
        )
}