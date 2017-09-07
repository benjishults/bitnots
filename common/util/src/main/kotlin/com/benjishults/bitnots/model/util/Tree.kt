package com.benjishults.bitnots.model.util


abstract class TreeNode<T : TreeNode<T>>(var parent: T?, val children: MutableList<T> = mutableListOf()) {

    fun toAncestors(function: (T) -> Unit) {
        function(this as T)
        val parentT = parent
        parentT?.let {
            parentT.toAncestors(function)
        }
    }

    fun breadthFirst(function: (T) -> Boolean): T? {
        return breadthFirstHelper(Queue<T>().also {
            it.enqueue(this as T)
        }, function)
    }

    // queue is not empty
    private tailrec fun breadthFirstHelper(queue: Queue<T>, function: (T) -> Boolean): T? {
        val node = queue.dequeue()
        if (function(node)) {
            return node
        } else {
            node.children.forEach {
                queue.enqueue(it)
            }
            if (queue.isEmpty()) {
                return null;
            }
            return breadthFirstHelper(queue, function)
        }
    }

    fun preOrder(function: (T) -> Boolean): T? {

        if (function(this as T)) {
            return this
        } else {
            children.forEach { n ->
                n.preOrder(function)?.also { return it }
            }
        }
        return null
    }

    fun preOrderWithPath(function: (T, List<Int>) -> Boolean): T? =
            preOrderWithPathHelper(function, listOf(0))

    private fun preOrderWithPathHelper(function: (T, List<Int>) -> Boolean, path: List<Int>): T? {
        if (function(this as T, path)) {
            return this
        } else {
            children.forEachIndexed { i, n ->
                n.preOrderWithPathHelper(function, path + i)?.also {
                    return it
                }
            }
        }
        return null
    }

    fun allLeaves(): List<T> {
        val value: MutableList<T> = mutableListOf()
        breadthFirstHelper(Queue<T>().also {
            it.enqueue(this as T)
        }) {
            if (it.children.isEmpty())
                value.add(it);
            return@breadthFirstHelper false;
        }
        return value
    }
}