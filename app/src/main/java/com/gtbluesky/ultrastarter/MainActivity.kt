package com.gtbluesky.ultrastarter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        Log.d(MainActivity::class.java.simpleName, "cost time=${System.currentTimeMillis() - MyApplication.startTime}")
//        Handler(Looper.getMainLooper()).postDelayed({
//            Log.d(this::class.java.simpleName, "allowPrivacyGrant")
//            AppStarterManager.allowPrivacy()
//        }, 1000)
    }
}