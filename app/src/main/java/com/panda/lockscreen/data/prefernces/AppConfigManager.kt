package com.panda.lockscreen.data.prefernces

import android.content.Context
import com.panda.reminderlockscreen.model.LockScreen
import com.panda.lockscreen.data.utils.Constants
import org.json.JSONArray

class AppConfigManager private constructor(context: Context) : Preferences(context, "Base_App") {
    companion object {
        @Volatile
        private var INSTANCE: AppConfigManager? = null

        fun initialize(context: Context): AppConfigManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppConfigManager(context).also { INSTANCE = it }
            }
        }

        fun getInstance(): AppConfigManager {
            return INSTANCE
                ?: throw IllegalStateException("RemoteManager is not initialized. Call initialize() first.")
        }
    }


    var remoteContentJson by stringPref(Constants.RemoteConfig.REMOTE_CONTENT_LOCK_SCREEN, "{}")


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


}