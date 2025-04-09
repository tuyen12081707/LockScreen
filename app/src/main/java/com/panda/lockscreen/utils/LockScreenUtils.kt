package com.panda.lockscreen.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.longToDateString(pattern: String = "dd/MM/yyyy HH:mm"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(this))
}
