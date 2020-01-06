package com.benjishults.bitnots.util.collection

import java.util.*

class Stack<T : Any>() {

    private var top: LinkedNode<T>? = null

    fun isEmpty() = top === null

    fun push(userObject: T) {
        top = LinkedNode(userObject, top)
    }

    fun pop(): T {
        var value: T? = null
        top = top?.run {
            value = userObject
            next
        }
        value?.let {
            return it
        } ?: throw EmptyStackException()
    }

    fun peek(): T {
        top = top?.run {
            return userObject
        }
        throw EmptyStackException()
    }

}
