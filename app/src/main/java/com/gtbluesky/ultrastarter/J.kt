package com.gtbluesky.ultrastarter

import android.content.Context
import com.gtbluesky.ultrastarter.task.AppInitializer
import com.gtbluesky.ultrastarter.task.DispatcherType

class J : AppInitializer<Unit>() {
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
        Thread.sleep(500)
    }

    override fun dependencies(): List<Class<out AppInitializer<*>>>? {
        return listOf(I::class.java)
    }

    override fun needPrivacyGrant(): Boolean {
        return true
    }
}