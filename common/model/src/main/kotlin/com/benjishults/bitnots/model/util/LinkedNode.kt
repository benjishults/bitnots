package com.benjishults.bitnots.model.util

class LinkedNode<T : Any>(val userObject: T, var next: LinkedNode<T>? = null) {
}