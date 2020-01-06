package com.benjishults.bitnots.util.collection

/**
 * Not thread safe
 */
class Queue<T : Any>() {

    private var first: LinkedNode<T>? = null
    private var last: LinkedNode<T>? = null

    fun isEmpty() = first == null

    fun enqueue(userObject: T) {
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
