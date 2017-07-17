package com.benjishults.bitnots.engine.prover

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.createSignedFormula
import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode

class Prover {

	fun prove(formula: Formula) {
		val tableau = Tableau(TableauNode( ArrayList<SignedFormula<out Formula>>().also {
			it.add(formula.createSignedFormula())
		}, null))
		println(tableau)
		println("is ${if (!tableau.isClosed()) "not" else ""} closed")
	}
}