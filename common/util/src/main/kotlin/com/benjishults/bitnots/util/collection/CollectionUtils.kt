package com.benjishults.bitnots.util.collection

import java.util.*

fun <T : Any> MutableList<T>.clone(): MutableList<T> =
        mutableListOf<T>().also {
            it.addAll(this)
        }

// indicate that we are treating a MutableList like a stack.

fun <T : Any> MutableList<T>.peek(): T =
        lastOrNull() ?: throw EmptyStackException()

fun <T : Any> MutableList<T>.push(t: T) = add(t)

fun <T : Any> MutableList<T>.pop(): T =
        lastOrNull()?.also {
            this.removeAt(this.lastIndex)
        } ?: throw EmptyStackException()
