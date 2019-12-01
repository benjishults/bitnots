package com.benjishults.bitnots.model.formulas.fol

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.formulas.propositional.LogicalOperators
import com.benjishults.bitnots.model.terms.BoundVariable


class ForSome(vararg variables: BoundVariable, formula: Formula<*>) :
        VarsBindingFormula(FormulaConstructor.intern(LogicalOperators.`for-some`.name), *variables, formula = formula)
