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
import com.panda.lockscreen.utils.createDailyReminderSchedule
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
                requestNotification(this, it)
            }
        }
    }

    fun requestNotification(
        context: Context,
        mLauncher: ActivityResultLauncher<String>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API 33)
            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                mLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            doWeHavePermissionFor(
                this,
                Manifest.permission.POST_NOTIFICATIONS
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
        val interValstring = "1,3,5,7"
        val intervals = interValstring.split(",").map { it.trim().toInt() }

        val titleIds = listOf(
            R.string.reminder_title_1,
            R.string.reminder_title_2,
            R.string.reminder_title_3,
            R.string.reminder_title_4
        )
        val subTitleIds = listOf(
            R.string.reminder_subtitle_1,
            R.string.reminder_subtitle_2,
            R.string.reminder_subtitle_3,
            R.string.reminder_subtitle_4
        )
        val imageIds =
            listOf(
                R.drawable.img_reminder,
                R.drawable.img_reminder,
                R.drawable.img_reminder,
                R.drawable.img_reminder
            )
        viewModel.setupReminders(
            context = this,
            intervals = intervals,
            titleIds = titleIds,
            subTitleIds = subTitleIds,
            imageIds = imageIds,
            isPresent = true
        )
    }


}