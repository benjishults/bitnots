package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.FormulaConstructor

object Truth : AtomicPropositionalFormula(FormulaConstructor.intern(LogicalOperators.`true`.name))
