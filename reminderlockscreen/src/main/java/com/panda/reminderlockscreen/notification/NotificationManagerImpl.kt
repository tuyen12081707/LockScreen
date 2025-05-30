package com.panda.reminderlockscreen.notification

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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.panda.reminderlockscreen.R
import com.panda.reminderlockscreen.presentation.activity.MainActivity


interface INotification {

    fun createNotification(event: String)
    fun cancelNotification()
}

class NotificationManagerImpl(
    private val context: Context, private val schedule: Schedule,
) :
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

    override fun createNotification(event: String) {
        Log.e("AlarmManagerImpl", "createNotification: ")
//

        val intentMain = Intent(context, MainActivity::class.java).apply {
            putExtra("isFromLockScreen", true)
            putExtra("event", event)
        }
        val pendingIntentMain = PendingIntent.getActivity(
            context,
            0,
            intentMain,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val reminderIntent = Intent(context, FullscreenReminderActivity::class.java).apply {
            putExtra("schedule_data", schedule)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            schedule.id,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app)
            .setContentTitle(schedule.title)
            .setContentText(schedule.content)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM) // Đặt category là CATEGORY_CALL
            .setFullScreenIntent(pendingIntent, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
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
        val activityIntent = Intent(context, MainActivity::class.java).apply {
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