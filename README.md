# 📱 LockScreen Reminder SDK

Thư viện hỗ trợ tạo lịch nhắc toàn màn hình (FullScreen Alarm) theo chu kỳ **tuần** hoặc **tháng**. Phiên bản mới đã được tối ưu hóa chuẩn "Plug & Play", hỗ trợ nhận cấu hình động từ **Firebase Remote Config** và tự động quản lý luồng điều hướng màn hình!

---

## ✅ TÍCH HỢP THƯ VIỆN

### 1. Thêm vào `build.gradle.kts` của `:app`

```kotlin
implementation("com.github.tuyen12081707:LockScreen:1.0.9")
```

> 🔧 Yêu cầu `targetSdk = 35`

---

### 2. Khai báo quyền trong `AndroidManifest.xml`

Thư viện đã tự động quản lý `FullscreenReminderActivity` ngầm bên trong. Bạn **KHÔNG CẦN** khai báo Activity nữa, chỉ cần cấp các quyền sau để Android cho phép bật sáng màn hình:

```xml
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />

<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.DISABLE_KEYGUARD" />

<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />

<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

### 3. Khởi tạo SDK (Sử dụng với Firebase Remote Config)

Bạn không cần tự parse JSON hay xử lý logic rườm rà. Chỉ cần gọi hàm `LockScreenSDK.init()` tại màn hình khởi động (Splash/Main) của app:

```kotlin
// 1. Lấy chuỗi cấu hình JSON từ Firebase Remote Config (hoặc API của bạn)
// Ví dụ: Key thiết lập trên Firebase là "remote_lock_screen"
val jsonRemoteConfig = mFirebaseRemoteConfig.getString("remote_lock_screen")

// 2. Khởi tạo SDK và chỉ định Màn hình muốn mở khi user bấm nút hành động
LockScreenSDK.init(
  context = this,
  remoteConfigJson = jsonRemoteConfig,
  targetActivity = MainActivity::class.java // Thay bằng Class màn hình đích của bạn
)
```

---

### 4. Cấu trúc chuỗi JSON (Firebase Remote Config)

Copy cấu trúc mảng dưới đây làm giá trị cho key `remote_lock_screen` trên Firebase:

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

#### 📌 Ghi chú ý nghĩa các trường:
| Trường | Dữ liệu | Ý nghĩa |
|---|---|---|
| `type` | `"week"` / `"month"` | Chu kỳ lặp theo tuần hoặc theo tháng. |
| `day` | `1-7` hoặc `1-31` | Nếu là week: 1 (CN) đến 7 (Thứ 7). Nếu là month: Ngày trong tháng. |
| `repeatTimes` | `1` / `0` | `1`: Lặp lại mãi mãi. `0`: Chỉ nhắc 1 lần duy nhất rồi hủy. |
| `event` | `String` | Sự kiện trả về App để xử lý Analytics hoặc Deep routing. |

---

### 5. Xử lý sự kiện (Event Tracking) khi mở App từ Màn hình khóa

Khi người dùng bấm "Bắt đầu" trên màn hình khóa, SDK sẽ tự động mở `targetActivity` (ví dụ: `MainActivity`) kèm theo chuỗi `event` đã cấu hình trong JSON.

Do Activity chính thường có `launchMode="singleTask"`, bạn cần hứng Intent ở cả `onCreate` (khi app mở mới) và `onNewIntent` (khi app đang chạy ngầm được gọi lên) để không bị sót log Firebase.

**Ví dụ trong `MainActivity.kt`:**

```kotlin
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
// import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Check intent khi app được mở mới hoàn toàn
        handleLockScreenIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent) // Cập nhật lại intent mới cho Activity
        
        // 2. Check intent khi app đang chạy ngầm và được gọi lên
        intent?.let { handleLockScreenIntent(it) }
    }

    private fun handleLockScreenIntent(intent: Intent) {
        val isFromLockScreen = intent.getBooleanExtra("isFromLockScreen", false)
        
        if (isFromLockScreen) {
            val eventName = intent.getStringExtra("lockscreen_event") ?: "unknown_event"
            Log.d("MainActivity", "User mở app từ LockScreen. Event: \$eventName")

            // Demo bắn log Firebase Analytics
            /*
            val analytics = FirebaseAnalytics.getInstance(this)
            val bundle = Bundle().apply {
                putString("source", "lock_screen_reminder")
            }
            analytics.logEvent(eventName, bundle)
            */
            
            // Xóa cờ để tránh handle lại khi xoay màn hình
            intent.removeExtra("isFromLockScreen") 
        }
    }
}

```
### 6. Tùy biến Giao diện (Override UI)

Thư viện hỗ trợ bạn tự thiết kế lại 100% giao diện màn hình khóa mà không cần can thiệp vào code logic.

**Cách làm:**
1. Trong thư mục `app/src/main/res/layout/` của bạn, tạo một file mới có tên **chính xác** là: `activity_full_screen_reminder.xml` (Trùng tên với file của SDK).
2. Thiết kế giao diện theo ý muốn của bạn.
3. **⚠️ Bắt buộc:** Để SDK có thể truyền dữ liệu và gắn sự kiện click, file layout của bạn **phải có chứa các View với ID y hệt** như sau (bạn có thể ẩn đi bằng `android:visibility="gone"` nếu không muốn dùng, nhưng phải có ID):
  * `@id/tvDay` (TextView - Hiển thị ngày)
  * `@id/tvTitle` (TextView - Tiêu đề)
  * `@id/tvSubTitle` (TextView - Nội dung)
  * `@id/imgAddPhoto` (ImageView - Ảnh minh họa)
  * `@id/btnOpenApp` (View/Button - Nút bấm mở app)
  * `@id/btnClose` (View/Button - Nút tắt báo thức)
---

## 📧 Liên hệ hỗ trợ

Nếu bạn gặp lỗi hoặc cần tuỳ chỉnh thêm, hãy tạo issue hoặc liên hệ trực tiếp.

> Viết bởi [tuyen12081707](https://github.com/tuyen12081707) – Vui lòng star repo nếu thấy hữu ích! 🌟