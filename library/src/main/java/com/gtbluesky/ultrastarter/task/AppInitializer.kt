package com.gtbluesky.ultrastarter.task

import android.util.Log
import java.util.concurrent.CountDownLatch

abstract class AppInitializer<T> : Initializer<T>, Dispatcher {
    private val countDownLatch: CountDownLatch? by lazy {
        if (dependenciesCount() == 0) null else CountDownLatch(dependenciesCount())
    }

    override fun dependenciesCount() = dependencies()?.size ?: 0

    override fun waitLatch() {
        try {
            countDownLatch?.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun notifyLatch() {
        countDownLatch?.countDown()
    }
}