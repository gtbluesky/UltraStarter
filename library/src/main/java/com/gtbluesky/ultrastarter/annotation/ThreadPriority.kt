package com.gtbluesky.ultrastarter.annotation

import android.os.Process

@Retention
@Target(AnnotationTarget.CLASS)
annotation class ThreadPriority(val priority: Int = Process.THREAD_PRIORITY_DEFAULT)
