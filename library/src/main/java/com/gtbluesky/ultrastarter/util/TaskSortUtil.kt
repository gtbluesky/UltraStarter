package com.gtbluesky.ultrastarter.util

import com.gtbluesky.ultrastarter.graph.DAGraph
import com.gtbluesky.ultrastarter.task.AppInitializer

internal object TaskSortUtil {
    @JvmStatic
    fun getTaskSort(taskList: List<AppInitializer<*>>?, ): List<AppInitializer<*>>? {
        if (taskList.isNullOrEmpty()) return null
        val clzList = taskList.map { it::class.java }
        val graph = DAGraph(taskList.size)
        taskList.forEachIndexed { i, task ->
            task.dependencies()?.forEach {
                val index = clzList.indexOf(it)
                graph.addEdge(index, i)
            }
        }
        return graph.topologySorting().map { taskList[it] }
    }
}