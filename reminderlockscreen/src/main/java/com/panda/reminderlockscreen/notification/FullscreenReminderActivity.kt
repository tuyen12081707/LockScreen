package com.panda.reminderlockscreen.notification

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.panda.reminderlockscreen.R
import com.panda.reminderlockscreen.databinding.ActivityFullScreenReminderBinding

class FullscreenReminderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenReminderBinding
    private var schedule: Schedule? = null

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("FullscreenReminder", "onNewIntent called")
        updateScheduleFromIntent(intent)
        setupUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        runCatching {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

        updateScheduleFromIntent(intent)
        setupKeyguardAndWakeLock()
        setupUI()
        setupListeners()
    }

    private fun updateScheduleFromIntent(intent: Intent) {
        schedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("schedule_data", Schedule::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("schedule_data")
        }
    }

    private fun setupKeyguardAndWakeLock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }
    }

    private fun setupUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvDay.text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd"))
        } else {
            binding.tvDay.text = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(Date())
        }

        schedule?.let {
            binding.tvTitle.text = it.title
            binding.tvSubTitle.text = it.content
            binding.btnOpenApp.text = it.buttonContent // Tối ưu thêm việc lấy text button

            if (it.imageUrl.isNotBlank()) {
                Glide.with(this)
                    .load(it.imageUrl.trim().toUri())
                    .placeholder(R.drawable.img_reminder)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(binding.imgAddPhoto)
            }
        }
    }

    private fun setupListeners() {
        binding.btnOpenApp.setOnClickListener {
            Log.d("FullscreenReminder", "User clicked open app. Event: ${schedule?.event}")
            openTargetActivity()
        }

        binding.btnClose.setOnClickListener {
            finishAffinity()
        }
    }

    private fun openTargetActivity() {
        runCatching {
            // 1. Lấy Class name đã lưu lúc init
            val prefs = getSharedPreferences("LockScreenSDK_Prefs", Context.MODE_PRIVATE)
            val targetClassName = prefs.getString("TARGET_ACTIVITY_CLASS", null)

            var targetIntent: Intent? = null

            if (!targetClassName.isNullOrBlank()) {
                try {
                    val clazz = Class.forName(targetClassName)
                    targetIntent = Intent(this, clazz)
                } catch (e: ClassNotFoundException) {
                    Log.e("FullscreenReminder", "Không tìm thấy Class: $targetClassName")
                }
            }

            // Fallback: Tìm Launcher Activity nếu không có targetClass
            if (targetIntent == null) {
                targetIntent = packageManager.getLaunchIntentForPackage(packageName)
            }

            targetIntent?.let {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                it.putExtra("isFromLockScreen", true)
                it.putExtra("event", schedule?.event)

                finish()
                startActivity(it)
            } ?: run {
                finishAffinity()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
        } else {
            @Suppress("DEPRECATION")
            window.clearFlags(
                android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        }
    }
}