package com.gtbluesky.ultrastarter.task

import android.content.Context

internal interface Initializer<T> {

    fun create(context: Context): T

    fun dependencies(): List<Class<out Initializer<*>>>?

    fun dependenciesCount(): Int

    fun needPrivacyGrant(): Boolean

    fun waitLatch()

    fun notifyLatch()

    fun waitOnMainThread(): Boolean

    fun dispatcherType(): DispatcherType

    fun onlyOnMainProcess(): Boolean
}