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
		return breadthFirstHelper(Queue<T>().also { it.enqueue(this as T) }, function)
	}

	// queue is not empty
	private tailrec fun breadthFirstHelper(queue: Queue<T>, function: (T) -> Boolean): T? {
		val node = queue.dequeue()
		if (function(node)) {
			return node
		} else {
			node.children.forEach { queue.enqueue(it) }
			if (queue.isEmpty()) {
				return null;
			}
			return breadthFirstHelper(queue, function)
		}
	}

	fun allLeaves(): List<T> {
		val value: MutableList<T> = mutableListOf()
		breadthFirstHelper(Queue<T>().also { it.enqueue(this as T) }) {
			if (it.children.isEmpty())
				value.add(it);
			return@breadthFirstHelper false;
		}
		return value
	}
}