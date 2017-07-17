package com.benjishults.bitnots.model.util

/**
 * Not thread safe
 */
class Queue<T : Any>() {

	var first: LinkedNode<T>? = null
	var last: LinkedNode<T>? = null

	fun isEmpty() = first == null

	fun enqueue( userObject: T) {
		var lFirst = first
		var lLast = last
		if (lFirst === null || lLast === null) {
			first = LinkedNode(userObject)
			last = first
		} else {
			lLast.next = LinkedNode(userObject)
			last = lLast.next
		}
	}

	fun dequeue(): T {
		var lFirst = first
		var lLast = last
		if (lFirst === null || lLast === null) {
			throw IllegalStateException("Cannot dequeue from an empty queue.")
		} else {
			val value = lFirst
			first = lFirst.next
			return value.userObject
		}
	}

}