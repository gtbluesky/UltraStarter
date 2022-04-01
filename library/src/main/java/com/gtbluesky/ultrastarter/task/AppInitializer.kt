package com.gtbluesky.ultrastarter.task

import java.util.concurrent.CountDownLatch

abstract class AppInitializer<T> : Initializer<T> {
    private val countDownLatch: CountDownLatch? by lazy {
        if (dependenciesCount() == 0) null else CountDownLatch(dependenciesCount())
    }

    final override fun dependenciesCount() = dependencies()?.size ?: 0

    override fun dependencies(): List<Class<out AppInitializer<*>>>? {
        return null
    }

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