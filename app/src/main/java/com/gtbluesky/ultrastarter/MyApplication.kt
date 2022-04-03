package com.gtbluesky.ultrastarter

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gtbluesky.ultrastarter.listener.CompleteListener

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
        val a = System.currentTimeMillis()
        AppStarter.Builder()
            .add(A::class.java)
            .add(B::class.java)
            .add(C::class.java)
            .add(D::class.java)
            .add(E::class.java)
            .add(F::class.java)
            .add(G::class.java)
            .add(H::class.java)
            .add(I::class.java)
            .add(J::class.java)
            .add(K::class.java)
            .add(L::class.java)
            .add(M::class.java)
            .setAwaitTimeout(3000)
            .setPrivacyGrant(true)
            .setLogEnabled(true)
            .setListener(object : CompleteListener {
                override fun onCompleted(costTime: Long) {
                    Log.d(this@MyApplication::class.java.simpleName, "任务全部完成，总耗时costTime=$costTime")
                }
            })
            .build(applicationContext)
            .start()
            .await()
        Log.d(this::class.java.simpleName, "appstarter cost=${System.currentTimeMillis() - a}")
//        startService(Intent(this, ProcessService::class.java))
    }
}