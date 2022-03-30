package com.gtbluesky.ultrastarter.task

internal interface Dispatcher {
    fun waitLatch()

    fun notifyLatch()

    fun waitOnMainThread(): Boolean

    fun dispatcherType(): DispatcherType

    fun onlyOnMainProcess(): Boolean
}