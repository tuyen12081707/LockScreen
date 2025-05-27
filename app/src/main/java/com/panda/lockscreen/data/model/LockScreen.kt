package com.panda.lockscreen.data.model

data class LockScreen(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val backgroundUrl: String = "",
    val image: String = "",
    val day: Int = 0,
    val hour: Int = 15,
    val mintues: Int = 0,
    val type: String = "",
    val repeatTimes: Int = 1,
    val event: String = ""
)
