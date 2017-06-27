package com.benjishults.bitnots.model.prover

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.inference.SignedFormula
import com.benjishults.bitnots.model.inference.createSignedFormula
import com.benjishults.bitnots.model.proof.Tableau
import com.benjishults.bitnots.model.proof.TableauNode

class Prover {

	fun prove(formula: Formula) {
		val tableau = Tableau(TableauNode( ArrayList<SignedFormula<out Formula>>().also {
			it.add(formula.createSignedFormula())
		}, null))
		println(tableau)
		println("is ${if (!tableau.isClosed()) "not" else ""} closed")
	}
}