package com.gtbluesky.ultrastarter

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ProcessService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}