package com.benjishults.bitnots.model.util

import java.util.Stack

@Suppress("UNCHECKED_CAST")
interface TreeNode {
    var parent: TreeNode?
    val children: MutableList<TreeNode>

    fun <T : TreeNode> toAncestors(function: (T) -> Unit) {
        function(this as T)
        parent?.let {
            it.toAncestors(function)
        }
    }

    fun <T : TreeNode> breadthFirst(function: (T) -> Boolean): T? {
        return breadthFirstHelper(Queue<T>().also {
            it.enqueue(this as T)
        }, function)
    }

    // queue is not empty
    private tailrec fun <T : TreeNode> breadthFirstHelper(queue: Queue<T>, function: (T) -> Boolean): T? {
        val node = queue.dequeue()
        if (function(node)) {
            return node
        } else {
            node.children.forEach {
                queue.enqueue(it as T)
            }
            if (queue.isEmpty()) {
                return null;
            }
            return breadthFirstHelper(queue, function)
        }
    }

    /**
     * returns a SkippableIterator that iterates through the tree rooted at the receiver but will skip the children of the last node when skip is called.
     */
    fun <T : TreeNode> preorderIterator(): SkippableIterator<T> {
        return object : SkippableIterator<T> {
            override var skipMode: Boolean = false

            /**
             * the node returned by the most recent call to next
             */
            private var prev: T? = null

            private val stack = Stack<T>().apply {
                push(this@TreeNode as T)
            }

            override fun next(): T {
                if (skipMode)
                    return skipToNext()
                else
                    prev?.let {
                        if (it.children.isNotEmpty()) {
                            it.children.reversed().forEach {
                                stack.push(it as T)
                            }
                        }
                    } ?: if (stack.isEmpty())
                        error("No next element")
                return stack.pop().also {
                    prev = it
                }
            }

            override fun hasSkipToNext(): Boolean {
                return stack.isNotEmpty()
            }

            override fun hasNext(): Boolean {
                if (skipMode) return hasSkipToNext()
                return stack.isNotEmpty() || prev.let {
                    it !== null &&
                            it.children.isNotEmpty()
                }
            }

            override fun skipToNext(): T {
                return stack.pop().also {
                    prev = it
                }
            }
        }
    }

    fun preorderSequence() =
            generateSequence {
                val stack = Stack<TreeNode>().apply {
                    add(this as TreeNode)
                }
                stack.pop().also {
                    it.children.reversed().forEach {
                        stack.push(it)
                    }
                }
            }

    fun <T : TreeNode> preOrder(function: (T) -> Boolean): T? {

        if (function(this as T)) {
            return this
        } else {
            children.forEach { n ->
                n.preOrder(function)?.also { return it }
            }
        }
        return null
    }

    fun <T : TreeNode> preOrderWithPath(function: (T, List<Int>) -> Boolean): T? =
            preOrderWithPathHelper(function, listOf(0))

    private fun <T : TreeNode> preOrderWithPathHelper(function: (T, List<Int>) -> Boolean, path: List<Int>): T? {
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

    fun <T : TreeNode> allLeaves(): List<T> {
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

open class TreeNodeImpl(
        override var parent: TreeNode?,
        override val children: MutableList<TreeNode> = mutableListOf()
) : TreeNode
