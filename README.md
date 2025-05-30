    implementation ("com.github.tuyen12081707:LockScreen:1.0.2")
- Tạo 1 file json như ví dụ ở trong assets/lockscreen.json
- Tạo 1 model Lockscreen
- ví dụ có 14 content thì đổi model type = "month" và set tay thành đủ 30 ngày chẳng hạn , xong nếu muốn lặp lại mỗi tháng để repeatTime = 1
- Ví dụ chỉ có 7 content thì đổi modle type = "week" và xong nếu muốn lặp lại mỗi tuần để repeatTime = 1
- Tạo activity FullscreenReminderActivity để chỉ định màn và sửa lại layout
Gọi Hàm 
     val listLock = AppConfigManager.getInstance().getLockScreenList()
        listLock.forEach {
            Log.d("TAG==", it.title)
        }
        ReminderScheduler.setupReminders(
            this,
            listLock,
        )
		    fun getLockScreenList(): ArrayList<LockScreen> {
        val list = ArrayList<LockScreen>()
        try {
            val jsonArray = remoteContentJson?.let { JSONArray(it) }
            if (jsonArray != null) {
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
                        buttonContent = item.optString(""),
                        mintues = item.optInt("minutes"),
                        type = item.optString("type"),
                        repeatTimes = item.getInt("repeatTimes"),
                        event = item.optString("event")
                    )
                    list.add(lockScreen)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
