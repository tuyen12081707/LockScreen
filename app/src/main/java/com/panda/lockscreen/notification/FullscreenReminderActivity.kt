package com.panda.lockscreen.notification

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.panda.lockscreen.databinding.ActivityFullScreenReminderBinding
import com.panda.lockscreen.presentation.activity.TestActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class FullscreenReminderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenReminderBinding
    private var schedule: Schedule? = null
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("FullScreenReminderReceiver", "onNewIntent called")

        schedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("schedule_data", Schedule::class.java)
        } else {
            intent.getParcelableExtra("schedule_data")
        }
        schedule?.let {
            binding.tvTitle.text = getString(it.titleId)
            binding.tvSubTitle.text = getString(it.subTitleId)
            binding.imgAddPhoto.setImageResource(it.imageId)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("FullScreenReminderReceiver", "onCreate")
        schedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("schedule_data", Schedule::class.java)
        } else {
            intent.getParcelableExtra("schedule_data")
        }
        schedule?.let {
            binding.tvTitle.text = getString(it.titleId)
            binding.tvSubTitle.text = getString(it.subTitleId)
            binding.imgAddPhoto.setImageResource(it.imageId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        }

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd")
            val formattedDate = currentDate.format(formatter)
            binding.tvDay.text = formattedDate
        } else {
            val currentDate = Date()
            val formatter = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
            val formattedDate = formatter.format(currentDate)
            binding.tvDay.text = formattedDate
        }

        binding.btnOpenApp.setOnClickListener {
            openMainActivity(1)
        }

        binding.icBtnClose.setOnClickListener {
            finishAffinity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Xóa các cờ liên quan đến màn hình khóa và bật màn hình
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
        } else {
            window.clearFlags(
                android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        }
    }

    private fun openMainActivity(source: Int) {
        val intent = Intent(this, TestActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("isFromLockScreen", true)
        }
        finish()
        startActivity(intent)
    }
}