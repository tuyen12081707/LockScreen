
# 📱 LockScreen Reminder SDK

Thư viện hỗ trợ tạo lịch nhắc toàn màn hình (FullScreen Alarm) theo chu kỳ **tuần** hoặc **tháng** bằng JSON cấu hình đơn giản.

---

## ✅ TÍCH HỢP THƯ VIỆN

### 1. Thêm vào `build.gradle.kts` của `:app`

```kotlin
implementation("com.github.tuyen12081707:LockScreen:1.0.2")
```

> 🔧 Yêu cầu `targetSdk = 35`

---

### 2. Tạo file cấu hình `assets/lockscreen.json`

```json
[
  {
    "id": 1,
    "title": "Nhắc nhở Thứ 2",
    "content": "Hôm nay bạn cần học gì?",
    "backgroundUrl": "https://example.com/bg1.png",
    "image": "https://example.com/img1.png",
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
| Số lượng nội dung | `type`  | `repeatTimes` | Ghi chú                         |
|-------------------|---------|----------------|----------------------------------|
| 7                 | "week"  | 1              | Lặp lại theo tuần               |
| 30                | "month" | 1              | Lặp lại theo tháng              |
| Tuỳ chỉnh         | "week"/"month" | 0 | Không lặp lại, chỉ 1 lần        |

---

### 3. Gọi SDK để lên lịch nhắc:

```kotlin
val listLock = AppConfigManager.getInstance().getLockScreenList()

listLock.forEach {
    Log.d("TAG==", it.title)
}

ReminderScheduler.setupReminders(
    context = this,
    listLockScreen = listLock
)
```

---

### 4. Cần tạo `FullscreenReminderActivity`

```kotlin
class FullscreenReminderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_reminder)

        val schedule = intent.getParcelableExtra<Schedule>("schedule_data")
        // TODO: hiển thị UI tùy theo dữ liệu
    }
}
```

#### Thêm vào AndroidManifest.xml:

```xml
<activity android:name=".FullscreenReminderActivity"
    android:exported="true"
    android:theme="@style/Theme.AppCompat.DayNight.NoActionBar"/>
```

---

### 5. Hàm đọc dữ liệu từ JSON

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
        e.printStackTrace()
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
