package com.benjishults.bitnots.model.formulas.fol

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.formulas.propositional.LogicalOperator
import com.benjishults.bitnots.model.terms.BoundVariable


class ForSome(vararg variables: BoundVariable, formula: Formula) :
        VarsBindingFormula(FormulaConstructor.intern(LogicalOperator.`for-some`.name), *variables, formula = formula)
