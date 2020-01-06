package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor

object Falsity : AtomicPropositionalFormula(PropositionalFormulaConstructor.intern(LogicalOperator.`false`.name))
