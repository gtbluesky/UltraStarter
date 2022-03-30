package com.gtbluesky.ultrastarter

import android.content.Context
import android.util.Log
import com.gtbluesky.ultrastarter.task.AppInitializer
import com.gtbluesky.ultrastarter.task.DispatcherType
import com.gtbluesky.ultrastarter.task.Initializer

class E: AppInitializer<Unit>() {
    override fun waitOnMainThread(): Boolean {
        return true
    }

    override fun dispatcherType(): DispatcherType {
        return DispatcherType.Default
    }

    override fun onlyOnMainProcess(): Boolean {
        return false
    }

    override fun create(context: Context) {
        Log.d(E::class.java.simpleName, "start:${System.currentTimeMillis()}")
        Thread.sleep(500)
        Log.d(E::class.java.simpleName, "end:${System.currentTimeMillis()}")
    }

    override fun dependencies(): List<Class<out Initializer<*>>>? {
        return listOf(A::class.java, B::class.java)
    }

    override fun needPrivacyGrant(): Boolean {
        return false
    }
}