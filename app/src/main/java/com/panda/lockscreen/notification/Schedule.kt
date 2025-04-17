package com.panda.lockscreen.notification

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
sealed class Schedule(
    open var id: Int,
    open val titleId: Int,
    open val subTitleId: Int,
    open val imageId: Int,
    open val repeatTimes: Int,
    open val hour: Int,
    open val minute: Int,
    open val units: Int,
    @DrawableRes open val imageBackup: Int? = null
) : Parcelable {
    @Parcelize
    data class ScheduleEachDay(
        override var id: Int,
        override val titleId: Int,
        override val subTitleId: Int,
        override val imageId: Int,
        override val repeatTimes: Int,
        override val hour: Int,
        override val minute: Int,
        override val units: Int,
        @DrawableRes override var imageBackup: Int? = null,
        val intervals: Int, // ✅ các mốc ngày sau (vd: [3, 7, 10])
        val createdAt: Long = System.currentTimeMillis() // ✅ ngày bắt đầu (mốc tính)
    ) : Schedule(
        id,
        titleId,
        subTitleId,
        imageId,
        repeatTimes,
        hour,
        minute,
        units,
        imageBackup
    )
    @Parcelize
    data class ScheduleDay(
        override var id: Int,
        override val titleId: Int,
        override val subTitleId: Int,
        override val imageId: Int,
        override val repeatTimes: Int,
        override val hour: Int=0,
        override val minute: Int=0,
        override val units: Int=0,
        @DrawableRes override var imageBackup: Int? = null,
        val time: Long,
    ) :
        Schedule(
            id,
            titleId,
            subTitleId,
            imageId,
            repeatTimes,
            hour,
            minute,
            units,
            imageBackup
        )

    @Parcelize
    data class ScheduleWeek(
        override var id: Int,
        override val titleId: Int,
        override val subTitleId: Int,
        override val imageId: Int,
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
            titleId,
            subTitleId,
            imageId,
            repeatTimes,
            hour,
            minute,
            units,
            imageBackup
        )

    @Parcelize
    data class ScheduleMonth(
        override var id: Int,
        override val titleId: Int,
        override val subTitleId: Int,
        override val imageId: Int,
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
            titleId,
            subTitleId,
            imageId,
            repeatTimes,
            hour,
            minute,
            units,
            imageBackup
        )
}