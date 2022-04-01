package com.gtbluesky.ultrastarter.task

import android.content.Context
import android.os.Process
import com.gtbluesky.ultrastarter.AppStarter
import com.gtbluesky.ultrastarter.annotation.ThreadPriority
import com.gtbluesky.ultrastarter.log.StarterLog

internal class StarterRunnable(
    private val context: Context,
    private val initializer: Initializer<*>,
    private val starter: AppStarter
) : Runnable {

    override fun run() {
        val startTime  = System.currentTimeMillis()
        StarterLog.d("${initializer::class.java.simpleName} start")
        Process.setThreadPriority(
            AppInitializer::class.java.getAnnotation(ThreadPriority::class.java)?.priority
                ?: Process.THREAD_PRIORITY_DEFAULT
        )
        initializer.waitLatch()
        val waitTime = System.currentTimeMillis()
        try {
            initializer.create(context)
        } catch (e: Exception) {
            StarterLog.e(initializer::class.java.simpleName, e)
        }
        val createTime = System.currentTimeMillis()
        if (initializer.waitOnMainThread() && initializer.dispatcherType() != DispatcherType.Main) {
            starter.notifyMain()
        }
        starter.notifyChildren(initializer)
        val msgMap = mapOf(
            "name" to initializer::class.java.simpleName,
            "waitTime" to (waitTime - startTime),
            "createTime" to (createTime - waitTime),
            "notifyTime" to (System.currentTimeMillis() - createTime)
        )
        StarterLog.d(StarterLog.msgJoin(msgMap))
        StarterLog.d("${initializer::class.java.simpleName} end")
    }
}