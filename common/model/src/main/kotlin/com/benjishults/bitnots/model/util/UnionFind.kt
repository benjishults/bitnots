package com.benjishults.bitnots.model.util

class UFNode<T>(val x: T) {
	var parent: UFNode<T> = this
	var rank = 0

	fun find(): UFNode<T> {
		if (parent === this)
			return this
		else {
			parent = parent.find()
			return parent
		}
	}

	fun union(other: UFNode<T>): UFNode<T> {
		val rep = this.find()
		val otherRep = other.find()
		if (rep !== otherRep) {
			val comp = rep.rank.compareTo(otherRep.rank)
			if (comp < 0) {
				rep.parent = otherRep
				return otherRep
			} else if (comp == 0) {
				rep.parent = otherRep
				otherRep.rank++
				return otherRep
			} else {
				otherRep.parent = rep
				return rep
			}
		} else
			return rep
	}

	override fun equals(other: Any?): Boolean {
		other?.let {
			if (other is UFNode<*>) {
				return other.x == x
			}
		}
		return false
	}

}

class EquivalenceClasses<T>(val relation: (T, T) -> Boolean) {
	val classes = mutableListOf<UFNode<T>>()

	fun add(t: T): UFNode<T> {
		classes.firstOrNull {
			relation(t, it.x)
		}?.also {
			return UFNode(t).union(it)
		}
		return UFNode(t).also { classes.add(it) }
	}

}
