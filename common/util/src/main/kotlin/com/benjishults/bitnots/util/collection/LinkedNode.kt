package com.benjishults.bitnots.util.collection


data class LinkedNode<T : Any>(val userObject: T, var next: LinkedNode<T>? = null) {

    // /**
    //  * Expensive.
    //  */
    // fun deepCopy(): LinkedNode<T> {
    //     val stack = Stack<T>().also { it.push(userObject) }
    //     var curr: LinkedNode<T>? = next
    //     while (curr !== null) {
    //         stack.push(curr.userObject)
    //         curr = curr.next
    //     }
    //     var value: LinkedNode<T>? = null
    //     while (!stack.isEmpty()) {
    //         value = LinkedNode(stack.pop(), value)
    //     }
    //     return value!!
    // }

}
