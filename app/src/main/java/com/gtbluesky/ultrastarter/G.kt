package com.gtbluesky.ultrastarter

import android.content.Context
import android.util.Log
import com.gtbluesky.ultrastarter.task.AppInitializer
import com.gtbluesky.ultrastarter.task.DispatcherType
import com.gtbluesky.ultrastarter.task.Initializer

class G: AppInitializer<Unit>() {
    override fun waitOnMainThread(): Boolean {
        return false
    }

    override fun dispatcherType(): DispatcherType {
        return DispatcherType.IO
    }

    override fun onlyOnMainProcess(): Boolean {
        return false
    }

    override fun create(context: Context) {
        Log.d(G::class.java.simpleName, "start:${System.currentTimeMillis()}")
        Thread.sleep(500)
        Log.d(G::class.java.simpleName, "end:${System.currentTimeMillis()}")
    }

    override fun dependencies(): List<Class<out Initializer<*>>>? {
        return listOf(E::class.java, F::class.java)
    }

    override fun needPrivacyGrant(): Boolean {
        return false
    }
}