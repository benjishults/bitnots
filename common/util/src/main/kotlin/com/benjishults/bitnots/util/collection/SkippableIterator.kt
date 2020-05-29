package com.benjishults.bitnots.util.collection

interface SkippableIterator<T> : Iterator<T> {
    /**
     * Skips zero or more elements to get to the next.  Resets [skipMode] to false.
     */
    fun skipToNext(): T

    var skipMode: Boolean

    /**
     * If skip mode is on, behaves like skipToNext().
     */
    override fun next(): T
    /**
     * If skip mode is on, this behaves like hasSkipToNext().
     */
    override fun hasNext(): Boolean

    /**
     * Returns true if skipToNext() would return a value.
     */
    fun hasSkipToNext(): Boolean
}
