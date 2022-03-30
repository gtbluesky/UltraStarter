package com.gtbluesky.ultrastarter.util

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process

internal object ProcessUtil {

    @JvmStatic
    fun isMainProcess(context: Context): Boolean {
        val packageName = context.packageName
        var processName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Application.getProcessName()
        } else {
            getProcessNameByActivityThread()
        }
        if (processName.isNullOrEmpty()) {
            processName = getProcessNameByActivityManager(context)
        }
        return packageName == processName
    }

    private fun getProcessNameByActivityThread(): String? {
        var processName: String? = null
        try {
            val clz = Class.forName(
                "android.app.ActivityThread",
                false,
                Thread.currentThread().contextClassLoader
            )
            val declaredMethod = clz.getDeclaredMethod("currentProcessName")
            val result = declaredMethod.invoke(null)
            if (result is String) {
                processName = result
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return processName
    }

    private fun getProcessNameByActivityManager(context: Context): String? {
        val pid = Process.myPid()
        return (context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.let {
            it.runningAppProcesses?.first { it.pid == pid }?.processName
        }
    }
}