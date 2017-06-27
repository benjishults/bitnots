package com.benjishults.bitnots.model.proof

import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.inference.AlphaFormula
import com.benjishults.bitnots.model.inference.ClosingFormula
import com.benjishults.bitnots.model.inference.SignedFormula
import com.benjishults.bitnots.model.util.Queue
import com.benjishults.bitnots.model.util.TreeNode
import com.benjishults.bitnots.model.inference.SimpleSignedFormula

class TableauNode(val newFormulas: MutableList<SignedFormula<out Formula>> = mutableListOf(),
				  parent: TableauNode?) : TreeNode<TableauNode>(parent) {

	// starts as proper ancestors and new oned are added after processing
	val allFormulas = ArrayList<SignedFormula<out Formula>>().also { list ->
		parent?.toAncestors { list.addAll(it.newFormulas.filter { it is SimpleSignedFormula<*> }) }
	}

	// apply alpha rules and regularity in place
	init {
		while (true) {
			val toAdd: MutableList<SignedFormula<out Formula>> = mutableListOf()
			newFormulas.iterator().let {
				while (it.hasNext()) {
					val current = it.next()
					if (current is AlphaFormula) {
						it.remove()
						toAdd.addAll(current.generateChildren());
					}
				}
			}
			if (toAdd.isEmpty())
				break
			else
				newFormulas.addAll(toAdd)
		}

		newFormulas.iterator().let { iter ->
			while (iter.hasNext()) {
				iter.next().let {
					if (it in allFormulas)
						iter.remove()
				}
			}
		}
		allFormulas.addAll(newFormulas)
	}

	var closed: Boolean = newFormulas.any { it is ClosingFormula } || hasCriticalPair()

	fun hasCriticalPair(): Boolean {
		val pos: MutableList<Prop> = mutableListOf()
		val neg: MutableList<Prop> = mutableListOf()
		allFormulas.map {
			if (it.formula is Prop) {
				if (it.sign)
					pos.add(it.formula)
				else
					neg.add(it.formula)
			}
		}
		return pos.any { p -> neg.any { it === p } }
	}

	fun isClosed(): Boolean {
		if (closed ||
				(children.isNotEmpty() && children.all { (it).isClosed() })) {
			closed = true
			return true
		} else
			return false
	}

	override fun toString(): String {
		return StringBuilder().also {
			if (newFormulas.any { it.sign })
				it.append("Suppose: " + newFormulas.filter { it.sign }.map { it.formula }.joinToString("\n") + "\n")
			if (newFormulas.any { !it.sign })
				it.append("Show: " + newFormulas.filter { !it.sign }.map { it.formula }.joinToString("\n") + "\n")
			if (children.isNotEmpty())
				children.joinToString("\n")
		}.toString();
	}

}
