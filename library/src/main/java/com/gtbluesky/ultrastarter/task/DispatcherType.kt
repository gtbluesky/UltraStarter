package com.gtbluesky.ultrastarter.task

enum class DispatcherType {
    /**
     * 主线程
     */
    Main,
    /**
     * 主线程空闲
     */
    Idle,
    /**
     * 适用于CPU密集型任务
     */
    Default,
    /**
     * 适用于IO密集型任务
     */
    IO
}