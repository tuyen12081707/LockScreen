package com.panda.lockscreen.presentation.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.panda.lockscreen.R
import com.panda.lockscreen.notification.Schedule
import com.panda.lockscreen.presentation.viewmodel.ReminderViewModel
import com.panda.lockscreen.utils.Constants
import org.koin.android.scope.getOrCreateScope
import org.koin.core.scope.Scope
import java.util.Calendar

class TestActivity : AppCompatActivity() {

    private val scope: Scope by getOrCreateScope()
    private val viewModel: ReminderViewModel by lazy(LazyThreadSafetyMode.NONE) {
        scope.get()
    }
    private var notificationLauncher: ActivityResultLauncher<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        notificationLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    handleInsertViewModel()
                }
            }

        if (isNotificationPermissionGranted()) {
            handleInsertViewModel()

        } else {
            notificationLauncher?.let {
                requestNotification(this,it)
            }
        }
    }
    fun requestNotification(
        context: Context,
        mLauncher: ActivityResultLauncher<String>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API 33)
            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                mLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            doWeHavePermissionFor(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            true
        }
    }
    fun doWeHavePermissionFor(context: Context, vararg permissions: String): Boolean {
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(context, it) !=
                PackageManager.PERMISSION_GRANTED
            ) return false
        }
        return true
    }
    private fun handleInsertViewModel() {

        val intervals = listOf(3, 7, 10, 20) // các mốc ngày nhắc

        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val schedule = Schedule.ScheduleEachDay(
            id = generateUniqueId(
                plantId = 1,
                dayOffset = intervals.first(),
                baseTime = currentTime
            ),
            plantId = 1,
            plantThumb = "your_image_url",
            plantName = "Aloe Vera",
            about = Constants.RemindAbout.WATERING,
            repeatTimes = intervals.size,
            hour = hour % 12,
            minute = minute,
            units = if (hour < 12) Constants.TimeUnit.AM else Constants.TimeUnit.PM,
            intervals = intervals,
            createdAt = currentTime
        )
        viewModel.setReminder(this,schedule)
    }

    fun generateUniqueId(
        plantId: Int,
        dayOffset: Int,
        baseTime: Long = System.currentTimeMillis()
    ): Int {
        val raw = "${plantId}_${dayOffset}_$baseTime"
        return raw.hashCode()
    }
}