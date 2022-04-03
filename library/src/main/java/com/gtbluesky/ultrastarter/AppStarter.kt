package com.gtbluesky.ultrastarter

import android.content.Context
import android.os.Looper
import com.gtbluesky.ultrastarter.exception.StarterException
import com.gtbluesky.ultrastarter.executor.ExecutorManager
import com.gtbluesky.ultrastarter.listener.CompleteListener
import com.gtbluesky.ultrastarter.log.StarterLog
import com.gtbluesky.ultrastarter.task.AppInitializer
import com.gtbluesky.ultrastarter.task.DispatcherType
import com.gtbluesky.ultrastarter.task.Initializer
import com.gtbluesky.ultrastarter.task.StarterRunnable
import com.gtbluesky.ultrastarter.util.ProcessUtil
import com.gtbluesky.ultrastarter.util.TaskSortUtil
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class AppStarter private constructor(
    private val context: Context,
    private val initializerList: List<Initializer<*>>,
    private val awaitTimeout: Long,
    private val awaitCount: Int,
    private val isPrivacyAllowed: Boolean,
    private val listener: CompleteListener? = null
) {

    private var mainCountDownLatch: CountDownLatch? = null
    private var privacyInitializerList = mutableListOf<Initializer<*>>()
    private val childrenMap = mutableMapOf<Initializer<*>, MutableList<Initializer<*>>?>()
    private val completedCount = AtomicInteger()
    private var startTime = 0L

    companion object {
        private const val AWAIT_TIMEOUT = 5000L
    }

    init {
        initializerList.forEach {
            if (!isPrivacyAllowed && it.needPrivacyGrant()) {
                privacyInitializerList.add(it)
            }
            it.dependencies()?.map { clz -> initializerList.first { it::class.java == clz } }
                ?.forEach { dependence ->
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
        startTime = System.currentTimeMillis()
        mainCountDownLatch = if (awaitCount == 0) null else CountDownLatch(awaitCount)
        if (!isPrivacyAllowed) {
            initializerList.filter { !it.needPrivacyGrant() }
        } else {
            initializerList
        }.let {
            dispatch(it)
        }
        StarterLog.d("Main thread dispatch cost time = ${System.currentTimeMillis() - startTime}")
    }

    internal fun startPrivacyInitializers() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw StarterException("startPrivacyInitializers() must be called in main thread.")
        }
        dispatch(privacyInitializerList)
    }

    private fun dispatch(list: List<Initializer<*>>) {
        var mainIdleExecutor: ExecutorManager.IdleStarter? = null
        TaskSortUtil.getTaskSort(list)?.forEach {
            val runnable = StarterRunnable(context, it, this)
            when (it.dispatcherType()) {
                DispatcherType.Main -> {
                    runnable.run()
                }
                DispatcherType.Idle -> {
                    mainIdleExecutor = mainIdleExecutor ?: ExecutorManager.mainIdleExecutor
                    mainIdleExecutor?.execute(runnable)
                }
                DispatcherType.Default -> {
                    ExecutorManager.computeExecutor.execute(runnable)
                }
                DispatcherType.IO -> {
                    ExecutorManager.ioExecutor.execute(runnable)
                }
            }
        }
        mainIdleExecutor?.start()
    }

    fun await() {
        try {
            mainCountDownLatch?.await(awaitTimeout, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        StarterLog.d("Main thread wait cost time = ${System.currentTimeMillis() - startTime}")
    }

    internal fun notifyChildren(initializer: Initializer<*>) {
        childrenMap[initializer]?.forEach {
            it.notifyLatch()
        }
        val count = completedCount.incrementAndGet()
        if (count == initializerList.size) {
            listener?.onCompleted(System.currentTimeMillis() - startTime)
        }
    }

    internal fun notifyMain() {
        mainCountDownLatch?.countDown()
    }

    class Builder {
        private val initializerClzList = arrayListOf<Class<out Initializer<*>>>()
        private var awaitTimeout = AWAIT_TIMEOUT
        private var isPrivacyAllowed = false
        private var listener: CompleteListener? = null

        fun add(clazz: Class<out AppInitializer<*>>) = apply {
            if (!initializerClzList.contains(clazz)) {
                initializerClzList.add(clazz)
            }
        }

        fun setAwaitTimeout(timeout: Long) = apply {
            awaitTimeout = timeout
        }

        fun setPrivacyGrant(isAllowed: Boolean) = apply {
            this.isPrivacyAllowed = isAllowed
        }

        fun setListener(listener: CompleteListener) = apply {
            this.listener = listener
        }

        fun setLogEnabled(enabled: Boolean) = apply {
            StarterLog.logEnabled = enabled
        }

        fun build(context: Context): AppStarter {
            var awaitCount = 0
            val currentProcessList = arrayListOf<Initializer<*>>()
            val initializerList = initializerClzList.map { it.newInstance() }
            if (ProcessUtil.isMainProcess(context)) {
                currentProcessList.addAll(initializerList)
            } else {
                currentProcessList.addAll(initializerList.filter { !it.onlyOnMainProcess() })
            }
            currentProcessList.forEach {
                if (needWaitOnMainThread(it)) {
                    ++awaitCount
                }
            }
            return AppStarter(
                context,
                currentProcessList,
                awaitTimeout,
                awaitCount,
                isPrivacyAllowed,
                listener
            ).also {
                AppStarterManager.putCache(it)
            }
        }
    }
}

internal fun needWaitOnMainThread(initializer: Initializer<*>): Boolean {
    return initializer.waitOnMainThread() && (initializer.dispatcherType() == DispatcherType.Default || initializer.dispatcherType() == DispatcherType.IO)
}