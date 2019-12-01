package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.FormulaConstructor

object Falsity : AtomicPropositionalFormula(FormulaConstructor.intern(LogicalOperators.`false`.name))
