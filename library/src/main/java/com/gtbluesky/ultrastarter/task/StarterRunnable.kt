package com.gtbluesky.ultrastarter.task

import android.content.Context
import android.os.Process
import com.gtbluesky.ultrastarter.AppStarter
import com.gtbluesky.ultrastarter.annotation.ThreadPriority

internal class StarterRunnable(
    private val context: Context,
    private val initializer: AppInitializer<*>,
    private val starter: AppStarter
) : Runnable {

    override fun run() {
        Process.setThreadPriority(
            AppInitializer::class.java.getAnnotation(ThreadPriority::class.java)?.priority
                ?: Process.THREAD_PRIORITY_DEFAULT
        )
        initializer.waitLatch()
        initializer.create(context)
        if (initializer.waitOnMainThread() && initializer.dispatcherType() != DispatcherType.Main) {
            starter.notifyMain()
        }
        starter.notifyChildren(initializer)
    }
}