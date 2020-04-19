package com.benjishults.bitnots.parser.formula

import com.benjishults.bitnots.theory.formula.AnnotatedFormula

interface FormulaClassifier {
    /**
     * @return a pair of lists of formulas: (hypotheses, targets)
     */
    fun classify(formulas: List<AnnotatedFormula>): HypsAndTargets
}

