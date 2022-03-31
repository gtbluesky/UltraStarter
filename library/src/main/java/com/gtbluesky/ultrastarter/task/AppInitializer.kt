package com.gtbluesky.ultrastarter.task

import java.util.concurrent.CountDownLatch

abstract class AppInitializer<T> : Initializer<T>, Dispatcher {
    private val countDownLatch: CountDownLatch? by lazy {
        if (dependenciesCount() == 0) null else CountDownLatch(dependenciesCount())
    }

    override fun dependenciesCount() = dependencies()?.size ?: 0

    final override fun waitLatch() {
        try {
            countDownLatch?.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    final override fun notifyLatch() {
        countDownLatch?.countDown()
    }
}