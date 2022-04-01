package com.gtbluesky.ultrastarter.util

import com.gtbluesky.ultrastarter.graph.DAGraph
import com.gtbluesky.ultrastarter.task.Initializer

internal object TaskSortUtil {
    @JvmStatic
    fun getTaskSort(taskList: List<Initializer<*>>?): List<Initializer<*>>? {
        if (taskList.isNullOrEmpty()) return null
        val clzList = taskList.map { it::class.java }
        val graph = DAGraph(taskList.size)
        taskList.forEachIndexed { i, task ->
            task.dependencies()?.forEach {
                val index = clzList.indexOf(it)
                if (index == -1) return@forEach
                graph.addEdge(index, i)
            }
        }
        return graph.topologySorting().map { taskList[it] }
    }
}