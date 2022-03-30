package com.gtbluesky.ultrastarter.executor

import android.os.Handler
import android.os.Looper
import java.util.concurrent.*

internal object ExecutorManager {
    /**
     * CPU密集型任务线程池
     */
    val computeExecutor: ThreadPoolExecutor

    /**
     * IO密集型任务线程池
     */
    val ioExecutor: ExecutorService

    /**
     * 主线程
     */
    val mainExecutor: Executor

    private val rejectedExecutionHandler = RejectedExecutionHandler { _, _ -> Executors.newCachedThreadPool(Executors.defaultThreadFactory()) }

    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val CORE_POOL_SIZE = CPU_COUNT + 1
    private val MAX_POOL_SIZE = CORE_POOL_SIZE
    private const val KEEP_ALIVE_TIME = 5L

    init {
        computeExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            LinkedBlockingDeque(),
            Executors.defaultThreadFactory(),
            rejectedExecutionHandler
        ).apply {
            allowCoreThreadTimeOut(true)
        }

        ioExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory())

        mainExecutor = object : Executor {
            private val handler = Handler(Looper.getMainLooper())

            override fun execute(command: Runnable) {
                handler.post(command)
            }
        }
    }
}