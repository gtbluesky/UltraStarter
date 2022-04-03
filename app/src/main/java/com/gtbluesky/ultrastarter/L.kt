package com.gtbluesky.ultrastarter

import android.content.Context
import android.widget.Toast
import com.gtbluesky.ultrastarter.task.AppInitializer
import com.gtbluesky.ultrastarter.task.DispatcherType

class L : AppInitializer<Unit>() {
    override fun create(context: Context) {
        Thread.sleep(20)
        Toast.makeText(context, "L", Toast.LENGTH_SHORT).show()
    }

    override fun needPrivacyGrant() = false

    override fun waitOnMainThread() = false

    override fun dispatcherType() = DispatcherType.Idle

    override fun onlyOnMainProcess() = true
}