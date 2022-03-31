package com.gtbluesky.ultrastarter

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log

class MyApplication : Application() {
    companion object{
        var startTime = 0L
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        startTime = System.currentTimeMillis()
    }

    override fun onCreate() {
        super.onCreate()
        AppStarter.Builder()
            .add(A())
            .add(B())
            .add(C())
            .add(D())
            .add(E())
            .add(F())
            .add(G())
            .add(H())
            .add(I())
            .add(J())
            .add(K())
            .setAwaitTimeout(3000)
            .setPrivacyGrant(false)
            .build(applicationContext)
            .start()
            .await()
//        startService(Intent(this, ProcessService::class.java))
    }
}