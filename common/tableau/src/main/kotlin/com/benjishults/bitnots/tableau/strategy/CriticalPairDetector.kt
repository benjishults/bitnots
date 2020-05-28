package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Substitution

interface CriticalPairDetector<out S> {

    fun criticalPair(
        positiveSignedFormula: PositiveSignedFormula<*>,
        negativeSignedFormula: NegativeSignedFormula<*>
    ): S

}

object FolCriticalPairDetector : CriticalPairDetector<Substitution> {
    override fun criticalPair(
        positiveSignedFormula: PositiveSignedFormula<*>,
        negativeSignedFormula: NegativeSignedFormula<*>
    ) =
        Formula.unify(positiveSignedFormula.formula, negativeSignedFormula.formula, EmptySub)
}

object PropositionalCriticalPairDetector : CriticalPairDetector<Boolean> {
    override fun criticalPair(
        positiveSignedFormula: PositiveSignedFormula<*>,
        negativeSignedFormula: NegativeSignedFormula<*>
    ) =
        positiveSignedFormula.formula == negativeSignedFormula.formula
}
