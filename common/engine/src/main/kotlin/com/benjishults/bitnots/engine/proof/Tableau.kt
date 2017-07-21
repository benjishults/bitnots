package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.engine.unifier.MultiBranchCloser

class Tableau(val root: TableauNode) {

	override fun toString() = root.toString()

	fun isClosed() = root.isClosed()

	fun unify(): MultiBranchCloser? {

		return null
	}

	fun step() {
		if (!applyDelta() && !applyBeta())
			applyGamma()
	}

	// TODO make this splice
	private fun applyDelta(): Boolean {
		var delta: DeltaFormula<out VarBindingFormula>? = null
		val node = root.breadthFirst {
			delta = it.newFormulas.firstOrNull { it is DeltaFormula<*> } as DeltaFormula<*>?
			delta !== null
		}
		if (node === null)
			return false
		delta?.let {
			delta ->
			node.newFormulas.remove(delta);
			val leaves = node.allLeaves()
			leaves.map { leaf -> delta.generateChildren().map { leaf.children.add(TableauNode(mutableListOf(it), leaf)) } }
			return true
		} ?: return false
	}

	// TODO make this splice
	private fun applyGamma(): Boolean {
		var gamma: GammaFormula<out VarBindingFormula>? = null
		val node = root.breadthFirst {
			gamma = it.newFormulas.firstOrNull { it is GammaFormula<*> } as GammaFormula<*>?
			gamma !== null
		}
		if (node === null)
			return false
		gamma?.let {
			gamma ->
			gamma.numberOfApplications++
			val leaves = node.allLeaves()
			leaves.map { leaf -> gamma.generateChildren().map { leaf.children.add(TableauNode(mutableListOf(it), leaf)) } }
			return true
		} ?: return false
	}

	private fun applyBeta(): Boolean {
		var beta: BetaFormula<out Formula<*>>? = null
		val node = root.breadthFirst {
			beta = it.newFormulas.firstOrNull { it is BetaFormula<*> } as BetaFormula<*>?
			beta !== null
		}
		if (node === null)
			return false
		beta?.let {
			beta ->
			node.newFormulas.remove(beta);
			val leaves = node.allLeaves()
			leaves.map { leaf -> beta.generateChildren().map { leaf.children.add(TableauNode(mutableListOf(it), leaf)) } }
			return true
		} ?: return false
	}

}
