package com.gtbluesky.ultrastarter

import android.content.Context
import com.gtbluesky.ultrastarter.task.AppInitializer
import com.gtbluesky.ultrastarter.task.DispatcherType

class E : AppInitializer<Unit>() {
    override fun waitOnMainThread(): Boolean {
        return false
    }

    override fun dispatcherType(): DispatcherType {
        return DispatcherType.Default
    }

    override fun onlyOnMainProcess(): Boolean {
        return false
    }

    override fun create(context: Context) {
        Thread.sleep(500)
    }

    override fun dependencies(): List<Class<out AppInitializer<*>>>? {
        return listOf(A::class.java, B::class.java)
    }

    override fun needPrivacyGrant(): Boolean {
        return false
    }
}