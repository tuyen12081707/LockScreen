# 📱 LockScreen Reminder SDK

Thư viện hỗ trợ tạo lịch nhắc toàn màn hình (FullScreen Alarm) theo chu kỳ **tuần** hoặc **tháng**. Phiên bản mới đã được tối ưu hóa chuẩn "Plug & Play", hỗ trợ nhận cấu hình động từ **Firebase Remote Config** và tự động quản lý luồng điều hướng màn hình!

---

## ✅ TÍCH HỢP THƯ VIỆN

### 1. Thêm vào `build.gradle.kts` của `:app`

```kotlin
implementation("com.github.tuyen12081707:LockScreen:1.1.0")
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

> **💡 Mẹo:** Khi màn hình đích được mở, bạn có thể lấy event của chiến dịch thông qua:
> `intent.getStringExtra("lockscreen_event")`

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

## 📧 Liên hệ hỗ trợ

Nếu bạn gặp lỗi hoặc cần tuỳ chỉnh thêm, hãy tạo issue hoặc liên hệ trực tiếp.

> Viết bởi [tuyen12081707](https://github.com/tuyen12081707) – Vui lòng star repo nếu thấy hữu ích! 🌟