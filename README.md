# 📱 LockScreen Reminder SDK (V2.0.0)

Thư viện nâng cao hỗ trợ đẩy lịch nhắc nhở theo 2 hình thức: **Toàn màn hình (Full Screen)** hoặc **Thông báo đẩy (Notification Only)**.
Hỗ trợ tương thích ngược với chuẩn JSON cũ (V1) và tích hợp thêm hệ thống thông minh (V2) với các tính năng: Cấu hình mốc giờ linh hoạt, Tự động xoay tua thay đổi nội dung (Dynamic Content Rotation), và Giới hạn số lần hiển thị trong ngày.

---

## ✅ TÍCH HỢP HỆ THỐNG

### 1. Thêm vào `build.gradle.kts` của `:app`

```kotlin
implementation("com.github.tuyen12081707:LockScreen:1.1.2")
```

### 2. Khai báo quyền bắt buộc trong `AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

## 🚀 KHỞI TẠO SDK (Chỉ 1 dòng duy nhất)

SDK hỗ trợ phân tích cấu hình tự động. Bạn chỉ cần gọi hàm `init` và truyền chuỗi JSON lấy từ Firebase Remote Config. SDK sẽ tự động nhận diện kịch bản V1 hay V2 để chạy.

```kotlin
// Yêu cầu: Dev cần tự xin quyền POST_NOTIFICATIONS và SYSTEM_ALERT_WINDOW trước khi gọi
val jsonConfig = mFirebaseRemoteConfig.getString("remote_lock_screen")

LockScreenSDK.init(
    context = this,
    remoteConfigJson = jsonConfig,
    targetActivity = MainActivity::class.java // Màn hình sẽ mở khi user bấm "Xem ngay"
)
```

---

## ⚙️ CẤU TRÚC JSON (FIREBASE REMOTE CONFIG)

Thư viện hỗ trợ 2 định dạng JSON. Khuyến nghị sử dụng **Phiên bản V2** để tận dụng tối đa sức mạnh của SDK.

### 🌟 Phiên bản V2 (Kịch bản thông minh - JSON Object)
Hỗ trợ quản lý tổng, giới hạn số lần bắn trong ngày và xoay vòng nội dung.

```json
{
  "enabled": true,
  "max_display_per_day": 5,
  "repeat_interval_minutes": 60,
  "target_hours": [13, 15, 19, 21],
  "contents": [
    {
      "id": 101,
      "title": "Học Tiếng Anh Thôi!",
      "content": "Mỗi ngày 5 từ vựng mới để nâng trình nhé.",
      "image": "[https://example.com/img1.png](https://example.com/img1.png)",
      "buttonContent": "Học ngay",
      "displayType": "FULL_SCREEN",
      "event": "lockscreen_study_eng"
    },
    {
      "id": 102,
      "title": "Uống nước đi bạn!",
      "content": "Đã đến lúc bổ sung nước cho cơ thể.",
      "displayType": "NOTIFICATION_ONLY",
      "event": "notify_drink_water"
    }
  ]
}
```
* **`max_display_per_day`**: Giới hạn số lần báo thức được kêu trong 1 ngày.
* **`repeat_interval_minutes`**: Sau khi báo thức kêu, nó sẽ tự lặp lại sau khoảng thời gian này (phút).
* **`target_hours`**: Các mốc giờ cố định trong ngày sẽ tự động quét để chạy.

---

### ⏳ Phiên bản V1 (Tương thích ngược - JSON Array)
Dành cho các chiến dịch cũ lặp lại theo tuần/tháng tĩnh.

```json
[
  {
    "id": 1,
    "title": "Nhắc nhở Thứ 2",
    "content": "Hôm nay bạn cần học gì?",
    "type": "week",
    "day": 2,
    "hour": 7,
    "minutes": 0,
    "displayType": "FULL_SCREEN",
    "repeatTimes": 1,
    "event": "monday_study"
  }
]
```

---

## 🎨 THUỘC TÍNH HIỂN THỊ (`displayType`)

Từ V2.0.0, bạn có thể điều khiển cách thức tiếp cận người dùng thông qua trường `"displayType"` trong JSON:

| Giá trị | Hành vi của SDK |
|---|---|
| `"FULL_SCREEN"`<br>*(Mặc định)* | Bắn thông báo Max Priority, đồng thời **mở bật sáng màn hình khóa** và hiển thị Activity đè lên trên kể cả khi máy đang tắt. Phù hợp cho nhắc nhở quan trọng. |
| `"NOTIFICATION_ONLY"` | Chỉ đẩy **thông báo đẩy (Push Notification)** lên thanh trạng thái âm thầm. Phù hợp cho các nhắc nhở nhẹ nhàng, tránh làm phiền user. |

---

## 🛠 TÙY BIẾN GIAO DIỆN MÀN HÌNH KHÓA

SDK cho phép bạn thiết kế lại 100% giao diện Full Screen mà không cần đụng vào code logic.
Tạo một file XML tại `app/src/main/res/layout/activity_full_screen_reminder.xml` trong dự án của bạn và thiết kế tùy ý.

**Bắt buộc phải chứa các ID sau (có thể ẩn đi bằng `visibility="gone"` nếu không dùng):**
* `@id/tvTitle`, `@id/tvSubTitle`, `@id/tvDay` (Các TextView)
* `@id/imgAddPhoto` (ImageView)
* `@id/btnOpenApp` (Nút bấm chính gọi Action)
* `@id/btnClose` (Nút tắt báo thức)

---

## 📊 TRACKING SỰ KIỆN (FIREBASE ANALYTICS)

Khi user bấm vào nút trên Màn hình khóa hoặc click vào Notification, SDK sẽ tự động mở `targetActivity` kèm theo Event ID cấu hình trong JSON.

Hứng sự kiện tại `MainActivity.kt`:

```kotlin
override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    handleLockScreenEvent(intent)
}

private fun handleLockScreenEvent(intent: Intent?) {
    if (intent?.getBooleanExtra("isFromLockScreen", false) == true) {
        val eventName = intent.getStringExtra("event") ?: "unknown"
        // Ghi log lên Firebase Analytics
        // firebaseAnalytics.logEvent(eventName, null)
    }
}

```
###  Các log event có sẵn trong app
- LockScreenSDK để check init có thành công không
- AlarmManagerImpl để check thời gian alarm record trả về nhé
