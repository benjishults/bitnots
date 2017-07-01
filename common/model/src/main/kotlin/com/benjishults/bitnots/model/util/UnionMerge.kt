package com.benjishults.bitnots.model.util

/**
 * [R] must be an equivalence relation on [T].
 */
class MergingEquivalenceClass<T>(val relation: (T, T) -> Boolean) {

	val classes = mutableSetOf<T>()

	fun add(t: T, merge: (T, T) -> T): T {
		classes.firstOrNull { relation(it, t) }?.let {
			return merge(it, t)
		} ?: return t.also { classes.add(it) }
	}

}
