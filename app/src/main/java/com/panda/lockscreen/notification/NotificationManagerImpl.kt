package com.panda.lockscreen.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.panda.lockscreen.presentation.activity.TestActivity
import com.panda.lockscreen.R


interface INotification {

    fun createNotification()
    fun cancelNotification()
}

class NotificationManagerImpl(private val context: Context,private val schedule: Schedule) :
    INotification {

    companion object {
        private const val CHANNEL_ID = "reminder_channel"
    }
    private fun createNotificationChannel(): NotificationManager {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Plant Notification Channel"
            val descriptionText = "This is my notification plant"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                name,
                importance
            ).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }

        return notificationManager
    }

    override fun createNotification() {
        Log.e("AlarmManagerImpl", "createNotification: ", )


        val intentMain = Intent(context, TestActivity::class.java).apply {
            putExtra("isFromLockScreen", true)
        }
        val pendingIntentMain = PendingIntent.getActivity(
            context,
            0,
            intentMain,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val reminderIntent = Intent(context, FullscreenReminderActivity::class.java).apply {
            putExtra("schedule_data",schedule)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            schedule.id,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app)
            .setContentTitle(context.getString(schedule.titleId))
            .setContentText(context.getString(schedule.subTitleId)) 
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL) // Đặt category là CATEGORY_CALL
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntentMain)
            .setAutoCancel(true)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val notificationManager = createNotificationChannel()
        with(NotificationManagerCompat.from(context)) {
            notificationManager.notify(schedule.id, notification.build())

        }
    }

    override fun cancelNotification() {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(schedule.id)
    }


    private fun getPendingIntentActivity(): PendingIntent {
        val activityIntent = Intent(context, TestActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }.apply {
            putExtra("open_from_notification", true)
        }
//        App.appResumeAdHelper?.setDisableAppResumeByClickAction()
        return PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE)
        if (notificationManager != null && notificationManager is NotificationManager) {
            notificationManager.cancel(1999)
        }
    }
}