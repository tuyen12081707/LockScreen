# --- Giữ lại tất cả class implement Parcelable ---
-keep class ** implements android.os.Parcelable { *; }

# --- Giữ lại CREATOR để Android đọc Parcel được ---
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# --- Giữ lại toàn bộ sealed class & subclass trong package của bạn ---
-keep class com.panda.reminderlockscreen.notification.** { *; }
-keep class com.panda.reminderlockscreen.model.** { *; }

# --- Giữ annotation của Kotlin Parcelize ---
-keepattributes *Annotation*

# --- Nếu có sử dụng Gson / Moshi / serialization ---
-keep class com.google.gson.** { *; }
-keep class kotlinx.serialization.** { *; }

# --- Nếu bạn dùng coroutines hoặc reflection ---
-keep class kotlinx.coroutines.** { *; }

# --- Nếu có Koin hoặc Dagger ---
-keep class org.koin.** { *; }
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
