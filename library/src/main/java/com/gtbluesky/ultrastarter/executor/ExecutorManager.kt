package com.gtbluesky.ultrastarter.executor

import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import java.util.*
import java.util.concurrent.*

internal object ExecutorManager {
    private val rejectedExecutionHandler =
        RejectedExecutionHandler { _, _ -> Executors.newCachedThreadPool(Executors.defaultThreadFactory()) }
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val CORE_POOL_SIZE = CPU_COUNT + 1
    private val MAX_POOL_SIZE = CORE_POOL_SIZE
    private const val KEEP_ALIVE_TIME = 5L

    /**
     * CPU密集型任务线程池
     */
    val computeExecutor: ThreadPoolExecutor by lazy {
        ThreadPoolExecutor(
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
    }

    /**
     * IO密集型任务线程池
     */
    val ioExecutor: ExecutorService by lazy {
        Executors.newCachedThreadPool(Executors.defaultThreadFactory())
    }

    /**
     * 主线程
     */
    val mainExecutor by lazy {
        object : Executor {
            private val handler = Handler(Looper.getMainLooper())

            override fun execute(command: Runnable) {
                handler.post(command)
            }
        }
    }

    /**
     * 主线程空闲
     */
    val mainIdleExecutor: IdleStarter
        get() {
            return object : IdleStarter {

                private val queue = ArrayDeque<Runnable>()

                private val idleHandler = MessageQueue.IdleHandler {
                    if (queue.isNotEmpty()) {
                        queue.poll()?.run()
                    }
                    queue.isNotEmpty()
                }

                override fun start() {
                    if (queue.isEmpty()) return
                    Looper.myQueue().addIdleHandler(idleHandler)
                }

                override fun execute(command: Runnable?) {
                    if (command == null) return
                    queue.offer(command)
                }
            }
        }

    interface IdleStarter : Executor {
        fun start()
    }
}