package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor

abstract class PropositionalFormula(cons: FormulaConstructor) : Formula(cons) {
	override abstract fun equals(other: Any?): Boolean
}
