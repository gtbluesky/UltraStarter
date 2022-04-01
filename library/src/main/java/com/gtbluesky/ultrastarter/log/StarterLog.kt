package com.gtbluesky.ultrastarter.log

import android.util.Log
import java.lang.StringBuilder

internal object StarterLog {
    var logEnabled = false

    private const val TAG = "StarterLog"

    fun d(msg: String) {
        if (!logEnabled) return
        Log.d(TAG, msg)
    }

    fun e(msg: String) {
        if (!logEnabled) return
        Log.e(TAG, msg)
    }

    fun e(msg: String, tr: Throwable) {
        if (!logEnabled) return
        Log.e(TAG, msg, tr)
    }

    fun msgJoin(msgMap: Map<String, Any>): String {
        val sb = StringBuilder()
        sb.append("Log content")
        sb.append("\n")
        sb.append("==================================================")
        sb.append("\n")
        msgMap.forEach { (k, v) ->
            sb.append("$k=$v")
            sb.append("\n")
        }
        sb.append("==================================================")
        return sb.toString()
    }
}