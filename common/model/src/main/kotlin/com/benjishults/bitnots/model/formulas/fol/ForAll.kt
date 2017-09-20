package com.benjishults.bitnots.model.formulas.fol

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.formulas.propositional.LogicalOperators
import com.benjishults.bitnots.model.terms.BoundVariable


class ForAll(vararg variables: BoundVariable, formula: Formula<*>) :
        VarBindingFormula(FormulaConstructor.intern(LogicalOperators.`for-all`.name), *variables, formula = formula)
