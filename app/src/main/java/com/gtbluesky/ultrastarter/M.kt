package com.gtbluesky.ultrastarter

import android.content.Context
import android.widget.Toast
import com.gtbluesky.ultrastarter.task.AppInitializer
import com.gtbluesky.ultrastarter.task.DispatcherType

class M : AppInitializer<Unit>() {
    override fun create(context: Context) {
        Thread.sleep(20)
        Toast.makeText(context, "M", Toast.LENGTH_SHORT).show()
    }

    override fun needPrivacyGrant() = true

    override fun waitOnMainThread() = false

    override fun dispatcherType() = DispatcherType.Idle

    override fun onlyOnMainProcess() = true
}