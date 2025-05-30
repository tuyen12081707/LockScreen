package com.panda.lockscreen.data.utils

object Constants {

    object RemoteConfig{
        const val REMOTE_CONTENT_LOCK_SCREEN="REMOTE_CONTENT_LOCK_SCREEN"
    }
    object TypeLockScreen{
        const val TYPE_WEEK="week"
        const val TYPE_MONTH = "month"
    }

    object TimeUnit {
        const val AM = 0
        const val PM = 1
    }

    object RemindAbout {
        const val WATERING = 1
        const val MISTING = 2
        const val ROTATING = 3
        const val FERTILIZING = 4
    }

    object Notification {
        const val CHANNEL_ID = "plant_notification_id"
        const val CHANNEL_NAME = "Plant Notification Channel"
        const val CHANNEL_DESCRIPTION = "This is my notification plant"
    }

    object IntentKeys {
        const val SCHEDULE_DATA = "schedule_data"
        const val OPEN_FROM_NOTIFICATION = "open_from_notification"
    }
}
