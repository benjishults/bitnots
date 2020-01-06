package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor

object Truth : AtomicPropositionalFormula(PropositionalFormulaConstructor.intern(LogicalOperator.`true`.name))
