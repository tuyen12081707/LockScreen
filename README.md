# 📱 LockScreen Reminder SDK

Thư viện hỗ trợ tạo lịch nhắc toàn màn hình (FullScreen Alarm) theo chu kỳ **tuần** hoặc **tháng** bằng JSON cấu hình đơn giản.

---

## ✅ TÍCH HỢP THƯ VIỆN

### 1. Thêm vào `build.gradle.kts` của `:app`

```kotlin
implementation("com.github.tuyen12081707:LockScreen:1.0.8")
```

> 🔧 Yêu cầu `targetSdk = 35`

---

### 2. Khai báo quyền trong `AndroidManifest.xml`

Để thư viện có thể bật sáng màn hình và hiển thị giao diện đè lên màn hình khóa một cách hợp lệ trên các bản Android mới, bạn bắt buộc phải thêm các quyền sau:

```xml
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />

<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.DISABLE_KEYGUARD" />

<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />

<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

### 3. Tạo file cấu hình `assets/lockscreen.json`

```json
[
  {
    "id": 1,
    "title": "Nhắc nhở Thứ 2",
    "content": "Hôm nay bạn cần học gì?",
    "backgroundUrl": "[https://example.com/bg1.png](https://example.com/bg1.png)",
    "image": "[https://example.com/img1.png](https://example.com/img1.png)",
    "day": 2,
    "hour": 7,
    "minutes": 0,
    "buttonContent": "Bắt đầu",
    "type": "week",
    "repeatTimes": 1,
    "event": "monday_study"
  }
]
```

#### 📌 Gợi ý cấu hình:
| Số lượng nội dung | `type`  | `repeatTimes` | Ghi chú                                  |
|-------------------|---------|----------------|----------------------------------|
| 7                 | "week"  | 1              | Lặp lại theo tuần                |
| 30                | "month" | 1              | Lặp lại theo tháng               |
| Tuỳ chỉnh         | "week"/"month" | 0 | Không lặp lại, chỉ 1 lần         |

---

### 4. Gọi Hàm ReminderScheduler để lên lịch nhắc

```kotlin
val listLock = AppConfigManager.getInstance().getLockScreenList()

listLock.forEach {
    Log.d("AlarmManagerImpl", "Lên lịch cho: ${it.title}")
}

ReminderScheduler.setupReminders(
    context = this,
    listLockScreen = listLock
)
```

---

### 5. Khởi tạo `FullscreenReminderActivity`

Tạo class hiển thị màn hình khóa như bên dưới:

```kotlin
class FullscreenReminderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenReminderBinding
    private var schedule: Schedule? = null

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("AlarmManagerImpl", "onNewIntent called")

        schedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("schedule_data", Schedule::class.java)
        } else {
            intent.getParcelableExtra("schedule_data")
        }
        handleNewIntent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("AlarmManagerImpl", "onCreate called")
        
        handleNewIntent()
        
        // Thiết lập cờ để Activity đè lên LockScreen và bật sáng màn hình
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(this, null)
        }

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
            Log.d("AlarmManagerImpl", "User clicked open app. Event: ${schedule?.event ?: "N/A"}")
            openMainActivity(1)
        }

        binding.btnClose.setOnClickListener {
            finishAffinity()
        }
    }

    private fun handleNewIntent() {
        schedule?.let {
            binding.tvTitle.text = it.title
            binding.tvSubTitle.text = it.content
            val imageUrl = it.imageUrl.trim().toUri()
            
            Glide.with(binding.imgAddPhoto)
                .load(imageUrl)
                .placeholder(R.drawable.img_reminder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(binding.imgAddPhoto)
                
            Glide.with(binding.main)
                .load(it.imageBackup)
                .placeholder(R.drawable.img_reminder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(binding.imgAddPhoto) 
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
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("isFromLockScreen", true)
        }
        finish()
        startActivity(intent)
    }
}
```

Và khai báo Activity này trong `AndroidManifest.xml` (Kèm theme Transparent để chuyển cảnh mượt hơn):

```xml
<activity 
    android:name=".data.FullscreenReminderActivity"
    android:exported="true"
    android:launchMode="singleTask"
    android:screenOrientation="portrait"
    android:theme="@style/Theme.Transparent"
    android:showOnLockScreen="true" />
```

---

### 6. Hàm đọc dữ liệu từ JSON

```kotlin
fun getLockScreenList(): ArrayList<LockScreen> {
    val list = ArrayList<LockScreen>()
    try {
        val json = context.assets.open("lockscreen.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val lockScreen = LockScreen(
                id = item.optInt("id"),
                title = item.optString("title"),
                content = item.optString("content"),
                backgroundUrl = item.optString("backgroundUrl"),
                image = item.optString("image"),
                day = item.optInt("day"),
                hour = item.optInt("hour"),
                mintues = item.optInt("minutes"),
                buttonContent = item.optString("buttonContent"),
                type = item.optString("type"),
                repeatTimes = item.optInt("repeatTimes"),
                event = item.optString("event")
            )
            list.add(lockScreen)
        }
    } catch (e: Exception) {
        Log.e("AlarmManagerImpl", "Lỗi đọc JSON LockScreen: ${e.message}")
    }
    return list
}
```

---

## 📌 Ghi chú quan trọng

- `day`:
    - Với `type = "week"`: từ 1 (CN) đến 7 (Thứ 7)
    - Với `type = "month"`: từ 1 đến 31
- `repeatTimes = 1`: nhắc lại (tuần/tháng).
- `repeatTimes = 0`: chỉ nhắc 1 lần duy nhất.

---

## 📧 Liên hệ hỗ trợ

Nếu bạn gặp lỗi hoặc cần tuỳ chỉnh thêm, hãy tạo issue hoặc liên hệ trực tiếp.

> Viết bởi [tuyen12081707](https://github.com/tuyen12081707) – Vui lòng star repo nếu thấy hữu ích! 🌟
