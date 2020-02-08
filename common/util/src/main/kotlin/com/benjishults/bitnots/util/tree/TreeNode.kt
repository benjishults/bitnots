package com.benjishults.bitnots.util.tree

import com.benjishults.bitnots.util.collection.Queue
import com.benjishults.bitnots.util.collection.SkippableIterator
import java.util.*

@Suppress("UNCHECKED_CAST")
interface TreeNode<TN: TreeNode<TN>> {
    var  parent: TN?
    val children: MutableList<TN>

    fun  toAncestors(function: (TN) -> Unit) {
        function(this as TN)
        parent?.toAncestors(function)
    }

    /**
     * Apply [function] breadth-first until it returns [true].
     * @return the node on which [function] returned [true] or [null] if none.
     */
    fun  breadthFirst(function: (TN) -> Boolean): TN? {
        return breadthFirstHelper(Queue<TN>().also {
            it.enqueue(this as TN)
        }, function)
    }

    // queue is not empty
    private tailrec fun  breadthFirstHelper(queue: Queue<TN>, function: (TN) -> Boolean): TN? {
        val node = queue.dequeue()
        if (function(node)) {
            return node
        } else {
            node.children.forEach {
                queue.enqueue(it)
            }
            if (queue.isEmpty()) {
                return null
            }
            return breadthFirstHelper(queue, function)
        }
    }

    /**
     * returns a SkippableIterator that iterates through the tree rooted at the receiver but will skip the children of the last node when skip is called.
     */
    fun  preorderIterator(): SkippableIterator<TN> {
        return object : SkippableIterator<TN> {
            override var skipMode: Boolean = false

            /**
             * the node returned by the most recent call to next
             */
            private var prev: TN? = null

            private val stack = Stack<TN>().apply {
                push(this@TreeNode as TN)
            }

            override fun next(): TN {
                if (skipMode)
                    return skipToNext()
                else
                    prev?.let {p ->
                        if (p.children.isNotEmpty()) {
                            p.children.forEach {
                                stack.push(it)
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

            override fun skipToNext(): TN {
                return stack.pop().also {
                    prev = it
                }
            }
        }
    }

    fun preorderSequence() =
            generateSequence {
                val stack = Stack<TN>().apply {
                    add(this as TN)
                }
                stack.pop().also {pop ->
                    pop.children.forEach {
                        stack.push(it)
                    }
                }
            }

    fun  preOrder(function: (TN) -> Boolean): TN? {

        if (function(this as TN)) {
            return this
        } else {
            children.forEach { n ->
                n.preOrder(function)?.also { return it }
            }
        }
        return null
    }

    fun  preOrderWithPath(function: (TN, List<Int>) -> Boolean): TN? =
            preOrderWithPathHelper(function, listOf(0))

    private fun  preOrderWithPathHelper(function: (TN, List<Int>) -> Boolean, path: List<Int>): TN? {
        if (function(this as TN, path)) {
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

    fun  allLeaves(): List<TN> {
        val value: MutableList<TN> = mutableListOf()
        breadthFirstHelper(Queue<TN>().also {
            it.enqueue(this as TN)
        }) {
            if (it.children.isEmpty())
                value.add(it)
            return@breadthFirstHelper false
        }
        return value
    }
}
