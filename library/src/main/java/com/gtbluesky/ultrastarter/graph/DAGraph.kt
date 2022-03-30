package com.gtbluesky.ultrastarter.graph

import java.util.ArrayDeque

/**
 * 有向无环图
 */
internal class DAGraph(private val vertexCount: Int) {
    /**
     * 使用邻接表存储DAG数据，邻接表索引即顶点索引
     * 对有向图而言，邻接是指一个节点所有出发的边
     * 在DAG中，A->B，表示A在B之前执行，即B依赖A
     */
    private val adjacencyList: MutableList<MutableList<Int>> = arrayListOf()

    init {
        repeat(vertexCount) {
            adjacencyList.add(arrayListOf())
        }
    }

    /**
     * 为DAG中添加一条从from点指向to点的边
     */
    fun addEdge(from: Int, to: Int) {
        adjacencyList[from].add(to)
    }

    fun topologySorting(): List<Int> {
        // 由邻接表构造入度表
        val inDegree = IntArray(vertexCount)
        adjacencyList.forEach {
            it.forEach { index ->
                inDegree[index]++
            }
        }
        // 从入度表取入度为0的节点并放入队列中
        val queue = ArrayDeque<Int>()
        inDegree.forEachIndexed { index, i ->
            if (i == 0) {
                queue.add(index)
            }
        }
        val result = arrayListOf<Int>()
        while (queue.isNotEmpty()) {
            val node = queue.poll()
            result.add(node)
            adjacencyList[node].forEach {
                if (--inDegree[it] == 0) {
                    queue.add(it)
                }
            }
        }
        // 检查DAG中是否有环
        check(result.size == vertexCount) {
            "Cycles in the DAG!"
        }
        return result
    }
}