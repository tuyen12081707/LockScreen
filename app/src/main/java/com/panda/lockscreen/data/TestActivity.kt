package com.panda.lockscreen.data

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.panda.lockscreen.R
import com.panda.lockscreen.data.prefernces.AppConfigManager
import com.panda.lockscreen.data.utils.Constants
import com.panda.reminderlockscreen.utils.ReminderScheduler

class TestActivity : AppCompatActivity() {

    private var notificationLauncher: ActivityResultLauncher<String>? = null
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val event = intent.getStringExtra("event") ?: ""
        Log.d("Event==", event)
    }

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
        requestRemoteConfig()
    }

    private fun requestRemoteConfig() {
        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setRemoteConfig(remoteConfig)
            }
            handleRequestNotification()

        }.addOnFailureListener {
            handleRequestNotification()
        }
    }

    private fun setRemoteConfig(remoteConfig: FirebaseRemoteConfig) {
        AppConfigManager.getInstance().remoteContentJson =
            remoteConfig.getString(Constants.RemoteConfig.REMOTE_CONTENT_LOCK_SCREEN)
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
        Log.d("TAG==", "handleInsertViewModel")
        val listLock = AppConfigManager.getInstance().getLockScreenList()
        listLock.forEach {
            Log.d("TAG==", it.title)
        }
        ReminderScheduler.setupReminders(
            this,
            listLock,
        )

    }


}