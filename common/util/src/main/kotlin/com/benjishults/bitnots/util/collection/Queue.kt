package com.benjishults.bitnots.util.collection

/**
 * Not thread safe
 */
class Queue<T : Any>() {

    private var first: LinkedNode<T>? = null
    private var last: LinkedNode<T>? = null

    fun isEmpty() = first == null

    fun enqueue(userObject: T) {
        val lFirst = first
        val lLast = last
        if (lFirst === null || lLast === null) {
            first = LinkedNode(userObject)
            last = first
        } else {
            lLast.next = LinkedNode(userObject)
            last = lLast.next
        }
    }

    fun dequeue(): T {
        val lFirst = first
        if (lFirst === null || last === null) {
            throw IllegalStateException("Cannot dequeue from an empty queue.")
        } else {
            first = lFirst.next
            return lFirst.userObject
        }
    }

}
