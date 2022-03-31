package com.gtbluesky.ultrastarter

import android.content.Context
import android.os.Looper
import com.gtbluesky.ultrastarter.exception.StarterException
import com.gtbluesky.ultrastarter.executor.ExecutorManager
import com.gtbluesky.ultrastarter.task.AppInitializer
import com.gtbluesky.ultrastarter.task.DispatcherType
import com.gtbluesky.ultrastarter.task.StarterRunnable
import com.gtbluesky.ultrastarter.util.ProcessUtil
import com.gtbluesky.ultrastarter.util.TaskSortUtil
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AppStarter private constructor(
    private val context: Context,
    private val initializerList: List<AppInitializer<*>>,
    private val awaitTimeout: Long,
    private val awaitCount: Int,
    private val isPrivacyAllowed: Boolean
) {

    private var mainCountDownLatch: CountDownLatch? = null
    private var privacyInitializerList = mutableListOf<AppInitializer<*>>()
    private val childrenMap = mutableMapOf<AppInitializer<*>, MutableList<AppInitializer<*>>?>()

    companion object {
        private const val AWAIT_TIMEOUT = 5000L
    }

    init {
        initializerList.forEach {
            if (!isPrivacyAllowed && it.needPrivacyGrant()) {
                privacyInitializerList.add(it)
            }
            it.dependencies()?.map { clz -> initializerList.first { it::class.java == clz }}?.forEach { dependence ->
                if (childrenMap[dependence] == null) {
                    childrenMap[dependence] = arrayListOf()
                }
                childrenMap[dependence]?.add(it)
            }
        }
    }

    fun start() = apply {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw StarterException("start() must be called in main thread.")
        }
        if (mainCountDownLatch != null) {
            throw StarterException("start() is called repeatedly.")
        }
        mainCountDownLatch = if (awaitCount == 0) null else CountDownLatch(awaitCount)
        if (!isPrivacyAllowed) {
            initializerList.filter { !it.needPrivacyGrant() }
        } else {
            initializerList
        }.let {
            dispatch(it)
        }
    }

    internal fun startPrivacyInitializers() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw StarterException("startPrivacyInitializers() must be called in main thread.")
        }
        dispatch(privacyInitializerList)
    }

    private fun dispatch(list: List<AppInitializer<*>>) {
        TaskSortUtil.getTaskSort(list)?.forEach {
            val runnable = StarterRunnable(context, it, this)
            when (it.dispatcherType()) {
                DispatcherType.Main -> {
                    runnable.run()
                }
                DispatcherType.Default -> {
                    ExecutorManager.computeExecutor.submit(runnable)
                }
                DispatcherType.IO -> {
                    ExecutorManager.ioExecutor.submit(runnable)
                }
            }
        }
    }

    fun await() {
        try {
            mainCountDownLatch?.await(awaitTimeout, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal fun notifyChildren(initializer: AppInitializer<*>) {
        childrenMap[initializer]?.forEach {
            it.notifyLatch()
        }
    }

    internal fun notifyMain() {
        mainCountDownLatch?.countDown()
    }

    class Builder {
        private val initializerList = arrayListOf<AppInitializer<*>>()
        private var awaitTimeout = AWAIT_TIMEOUT
        private var isPrivacyAllowed = false

        fun add(initializer: AppInitializer<*>) = apply {
            if (!initializerList.contains(initializer)) {
                initializerList.add(initializer)
            }
        }

        fun setAwaitTimeout(timeout: Long) = apply {
            awaitTimeout = timeout
        }

        fun setPrivacyGrant(isAllowed: Boolean) = apply {
            this.isPrivacyAllowed = isAllowed
        }

        fun build(context: Context): AppStarter {
            var awaitCount = 0
            val currentProcessList = arrayListOf<AppInitializer<*>>()
            if (ProcessUtil.isMainProcess(context)) {
                currentProcessList.addAll(initializerList)
            } else {
                currentProcessList.addAll(initializerList.filter { !it.onlyOnMainProcess() })
            }
            currentProcessList.forEach {
                if (it.waitOnMainThread() && it.dispatcherType() != DispatcherType.Main) {
                    ++awaitCount
                }
            }
            return AppStarter(
                context,
                currentProcessList,
                awaitTimeout,
                awaitCount,
                isPrivacyAllowed
            ).also {
                AppStarterManager.putCache(it)
            }
        }
    }
}