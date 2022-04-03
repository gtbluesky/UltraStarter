package com.gtbluesky.ultrastarter.util

import java.text.SimpleDateFormat
import java.util.*

internal object DateUtil {
    fun getCurrentFormatTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getFormatTime(timeMs: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return dateFormat.format(Date(timeMs))
    }
}