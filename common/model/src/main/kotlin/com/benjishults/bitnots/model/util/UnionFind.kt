package com.benjishults.bitnots.model.util

/**
 * [R] must be an equivalence relation on [T].
 */
class EquivalenceClass<T>(val relation: (T, T) -> Boolean) {

	private class Node<T>(val x: T) {
		var parent: Node<T> = this
		var rank = 0

		fun find(): Node<T> {
			if (parent === this)
				return this
			else {
				parent = parent.find()
				return parent
			}
		}

		fun union(other: Node<T>): Node<T> {
			val rep = this.find()
			val otherRep = other.find()
			if (rep !== otherRep) {
				val comp = rep.rank.compareTo(other.rank)
				if (comp < 0) {
					rep.parent = other
					return other
				} else if (comp == 0) {
					rep.parent = other
					other.rank++
					return other
				} else {
					other.parent = rep
					return rep
				}
			} else
				return rep
		}
	}

	private val classes = mutableSetOf<Node<T>>()

	fun add(t: T): T {
		classes.firstOrNull { relation(it.x, t) }?.let {
			return it.union(Node(t)).x
		} ?: return Node(t).also { classes.add(it) }.x
	}

}
