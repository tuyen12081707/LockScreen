package com.panda.reminderlockscreen.presentation.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.panda.reminderlockscreen.R
import com.panda.reminderlockscreen.model.LockScreen
import com.panda.reminderlockscreen.notification.FullscreenReminderActivity
import com.panda.reminderlockscreen.utils.Constants
import com.panda.reminderlockscreen.utils.ReminderScheduler

class MainActivity : AppCompatActivity() {

    private var notificationLauncher: ActivityResultLauncher<String>? = null
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val event = intent.getStringExtra("event")?:""
        Log.d("Event==",event)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
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
        handleRequestNotification()
    }





    private fun handleRequestNotification() {


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
        ReminderScheduler.setupReminders(this,getLockScreenList())

    }

    fun getLockScreenList(): ArrayList<LockScreen> {
        val list = ArrayList<LockScreen>()

        return list
    }





}