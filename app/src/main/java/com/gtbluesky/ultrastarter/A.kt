package com.gtbluesky.ultrastarter

import android.content.Context
import android.util.Log
import com.gtbluesky.ultrastarter.task.AppInitializer
import com.gtbluesky.ultrastarter.task.DispatcherType
import com.gtbluesky.ultrastarter.task.Initializer

class A: AppInitializer<Unit>() {
    override fun waitOnMainThread(): Boolean {
        return true
    }

    override fun dispatcherType(): DispatcherType {
        return DispatcherType.IO
    }

    override fun onlyOnMainProcess(): Boolean {
        return false
    }

    override fun create(context: Context) {
        Log.d(A::class.java.simpleName, "start:${System.currentTimeMillis()}")
        Thread.sleep(500)
        Log.d(A::class.java.simpleName, "end:${System.currentTimeMillis()}")
    }

    override fun dependencies(): List<Class<out Initializer<*>>>? {
        return null
    }

    override fun needPrivacyGrant(): Boolean {
        return false
    }
}